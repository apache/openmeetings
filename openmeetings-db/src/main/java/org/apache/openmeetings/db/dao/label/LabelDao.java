/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.db.dao.label;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.ROOT;
import static java.util.ResourceBundle.Control.FORMAT_PROPERTIES;
import static java.util.ResourceBundle.Control.getControl;
import static org.apache.openmeetings.db.util.ApplicationHelper._ensureApplication;
import static org.apache.openmeetings.db.util.DaoHelper.UNSUPPORTED;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.label.OmLanguage;
import org.apache.openmeetings.db.entity.label.StringLabel;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.XmlExport;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.util.string.Strings;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 *
 * CRUD operations for {@link StringLabel}
 *
 * @author solomax, swagner
 *
 */
public class LabelDao implements IDataProviderDao<StringLabel>{
	private static final Logger log = Red5LoggerFactory.getLogger(LabelDao.class, getWebAppRootKey());
	private static final String ENTRY_ELEMENT = "entry";
	private static final String KEY_ATTR = "key";
	public static final String APP_RESOURCES_PREFIX = "Application";
	public static final String APP_RESOURCES_SUFFIX = ".properties.xml";
	private static final LinkedHashMap<Long, OmLanguage> languages = new LinkedHashMap<>();
	private static final ConcurrentHashMap<Locale, List<StringLabel>> labelCache = new ConcurrentHashMap<>();
	private static final Set<String> keys = new HashSet<>();
	private static Class<?> appClass = null;

	private static void storeLanguages() throws Exception {
		Document d = XmlExport.createDocument();
		Element r = XmlExport.createRoot(d, "language");
		for (Map.Entry<Long, OmLanguage> e : languages.entrySet()) {
			r.addElement("lang").addAttribute("id", "" + e.getKey()).addAttribute("code", e.getValue().getLocale().toLanguageTag());
		}
		XmlExport.toXml(getLangFile(), d);
	}

	public static void add(Locale l) throws Exception {
		long id = 0L;
		for (Map.Entry<Long, OmLanguage> e : languages.entrySet()) {
			id = e.getKey();
		}
		languages.put(id + 1, new OmLanguage(l));
		storeLanguages();
		labelCache.put(l, new ArrayList<StringLabel>());
	}

	public static String getString(String key, long langId) {
		return _ensureApplication().getOmString(key, langId);
	}

	private static File getLangFile() {
		return new File(OmFileHelper.getLanguagesDir(), OmFileHelper.LANG_FILE_NAME);
	}

	public static synchronized Class<?> getAppClass() throws ClassNotFoundException {
		if (appClass == null) {
			//HACK to resolve package dependencies
			appClass = Class.forName("org.apache.openmeetings.web.app.Application");
		}
		return appClass;
	}

	public static void initLanguageMap() {
		SAXReader reader = new SAXReader();
		try {
			getAppClass();
			Document document = reader.read(getLangFile());
			Element root = document.getRootElement();
			languages.clear();
			for (Iterator<Element> it = root.elementIterator("lang"); it.hasNext();) {
				Element item = it.next();
				Long id = Long.valueOf(item.attributeValue("id"));
				String code = item.attributeValue("code");
				if (id == 3L) {
					continue;
				}
				languages.put(id, new OmLanguage(Locale.forLanguageTag(code)));
			}
		} catch (Exception e) {
			log.error("Error while building language map");
		}
	}

	public static String getLabelFileName(Locale l) {
		String name = getControl(FORMAT_PROPERTIES).toBundleName(APP_RESOURCES_PREFIX, ENGLISH.equals(l) ? ROOT : l);
		return String.format("%s%s", name, APP_RESOURCES_SUFFIX);
	}

	private static void storeLabels(Locale l) throws Exception {
		Document d = XmlExport.createDocument();
		Element r = XmlExport.createRoot(d);
		List<StringLabel> labels = new ArrayList<>(labelCache.get(l));
		Collections.sort(labels, new LabelComparator());
		for (StringLabel sl : labels) {
			r.addElement(ENTRY_ELEMENT).addAttribute(KEY_ATTR, sl.getKey()).addCDATA(sl.getValue());
		}
		URL u = appClass.getResource(getLabelFileName(l));
		XmlExport.toXml(new File(u.toURI()), d);
	}

	public static void upload(Locale l, InputStream is) throws Exception {
		List<StringLabel> labels = getLabels(is);
		URL u = appClass.getResource(getLabelFileName(Locale.ENGLISH)); //get the URL of existing resource
		File el = new File(u.toURI());
		File f = new File(el.getParentFile(), getLabelFileName(l));
		if (!f.exists()) {
			f.createNewFile();
		}
		labelCache.put(l, labels);
		storeLabels(l);
	}

	private static List<StringLabel> getLabels(Locale l) {
		List<StringLabel> labels = new ArrayList<>();
		try (InputStream is = appClass.getResourceAsStream(getLabelFileName(l))) {
			labels = getLabels(is);
		} catch (Exception e) {
			log.error("Error reading resources document", e);
		}
		return labels;
	}

	private static List<StringLabel> getLabels(InputStream is) throws IOException {
		final List<StringLabel> labels = new ArrayList<>();
		Properties props = new Properties();
		props.loadFromXML(is);
		props.forEach((k, v) -> labels.add(new StringLabel((String)k, (String)v)));
		return labels;
	}

	private static List<StringLabel> getLabels(Locale l, final String search) {
		if (!labelCache.containsKey(l)) {
			List<StringLabel> ll = getLabels(l);
			labelCache.putIfAbsent(l, ll);
		}
		List<StringLabel> result = new ArrayList<>(labelCache.containsKey(l) ? labelCache.get(l) : new ArrayList<StringLabel>());
		if (!Strings.isEmpty(search)) {
			CollectionUtils.filter(result, o -> o != null && (o.getKey().contains(search) || o.getValue().contains(search)));
		}
		return result;
	}

	@Override
	public StringLabel get(long id) {
		throw UNSUPPORTED;
	}

	@Override
	public StringLabel get(Long id) {
		throw UNSUPPORTED;
	}

	@Override
	public List<StringLabel> get(long start, long count) {
		throw UNSUPPORTED;
	}

	@Override
	public List<StringLabel> get(String search, long start, long count, String order) {
		throw UNSUPPORTED;
	}

	public static OmLanguage getLanguage(Long id) {
		OmLanguage l = id == null ? null : languages.get(id);
		return l == null ? languages.get(1L) : l;
	}

	public static Locale getLocale(Long id) {
		return getLanguage(id).getLocale();
	}

	public static Long getLanguage(Locale loc, Long def) {
		if (loc != null) {
			for (Map.Entry<Long, OmLanguage> e : languages.entrySet()) {
				if (loc.equals(e.getValue().getLocale())) {
					return e.getKey();
				}
			}
		}
		return def;
	}

	public static Set<Map.Entry<Long, Locale>> getLanguages() {
		return languages.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getLocale())).entrySet();
	}

	public static List<StringLabel> get(Locale l, final String search, long start, long count, final SortParam<String> sort) {
		List<StringLabel> result = getLabels(l, search);
		if (sort != null) {
			Collections.sort(result, new LabelComparator(sort));
		}
		return result.subList((int)start, (int)(start + count > result.size() ? result.size() : start + count));
	}

	@Override
	public long count() {
		throw UNSUPPORTED;
	}

	@Override
	public long count(String search) {
		throw UNSUPPORTED;
	}

	public static long count(Locale l, final String search) {
		return getLabels(l, search).size();
	}

	@Override
	public StringLabel update(StringLabel entity, Long userId) {
		throw UNSUPPORTED;
	}

	public static StringLabel update(Locale l, StringLabel entity) throws Exception {
		List<StringLabel> labels = labelCache.get(l);
		if (!labels.contains(entity)) {
			labels.add(entity);
			keys.add(entity.getKey());
		}
		storeLabels(l);
		return entity;
	}

	@Override
	public void delete(StringLabel entity, Long userId) {
		throw UNSUPPORTED;
	}

	public static void delete(Locale l, StringLabel entity) throws Exception {
		List<StringLabel> labels = labelCache.get(l);
		if (labels.contains(entity)) {
			labels.remove(entity);
			keys.remove(entity.getKey());
			storeLabels(l);
		}
	}

	public static void delete(Locale l) {
		for (Map.Entry<Long, OmLanguage> e : languages.entrySet()) {
			if (e.getValue().getLocale().equals(l)) {
				languages.remove(e.getKey());
				break;
			}
		}
		labelCache.remove(l);
		try {
			URL u = appClass.getResource(getLabelFileName(l));
			if (u != null) {
				File f = new File(u.toURI());
				if (f.exists()) {
					f.delete();
				}
			}
		} catch (Exception e) {
			log.error("Unexpected error while deleting language", e);
		}
	}

	private static class LabelComparator implements Comparator<StringLabel>, Serializable {
		private static final long serialVersionUID = 1L;
		final SortParam<String> sort;

		LabelComparator() {
			this.sort = new SortParam<>(KEY_ATTR, true);
		}

		LabelComparator(SortParam<String> sort) {
			this.sort = sort;
		}

		@Override
		public int compare(StringLabel o1, StringLabel o2) {
			int val;
			if (KEY_ATTR.equals(sort.getProperty())) {
				try {
					int i1 = Integer.parseInt(o1.getKey()), i2 = Integer.parseInt(o2.getKey());
					val = i1 - i2;
				} catch (Exception e) {
					val = o1.getKey().compareTo(o2.getKey());
				}
			} else {
				val = o1.getValue().compareTo(o2.getValue());
			}
			return (sort.isAscending() ? 1 : -1) * val;
		}
	}
}
