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
package org.apache.openmeetings.backup.converter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_FILE_ID;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_FILE_TYPE;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_SLIDE;
import static org.apache.openmeetings.db.dto.room.Whiteboard.ATTR_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_WML;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM__SRC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.openmeetings.db.dto.room.Whiteboard;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.util.OmFileHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

public class WbConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(WbConverter.class, getWebAppRootKey());
	private static final String ATTR_STROKE = "strokeWidth";
	private static final String ATTR_OPACITY = "opacity";
	private static final String TYPE_IMAGE = "image";

	private WbConverter() {
		//should not be used
	}

	private static String getColor(int val) {
		return String.format("#%06X", 0xFFFFFF & val);
	}

	private static void add(Whiteboard wb, JSONObject o) {
		String uid = UUID.randomUUID().toString();
		wb.put(uid, o.put("uid", uid));
	}

	private static JSONObject init(Whiteboard wb, List<?> props) {
		return init(wb, props, true);
	}

	private static JSONObject init(Whiteboard wb, List<?> props, boolean addDim) {
		double left = ((Number)props.get(props.size() - 5)).doubleValue();
		double top = ((Number)props.get(props.size() - 4)).doubleValue();
		double w = ((Number)props.get(props.size() - 3)).doubleValue();
		double h = ((Number)props.get(props.size() - 2)).doubleValue();
		JSONObject o = new JSONObject().put(ATTR_SLIDE, 0);
		if (addDim) {
			o.put("left", left)
				.put("top", top)
				.put("width", w)
				.put("height", h);
			wb.setWidth((int)Math.max(wb.getWidth(), w + left));
			wb.setHeight((int)Math.max(wb.getHeight(), h + top));
		}
		return o;
	}

	private static JSONObject setColor(JSONObject o, String stroke, String fill) {
		return o.put("stroke", stroke).put("fill", fill);
	}

	private static void processText(Whiteboard wb, List<?> props) {
		if (props.size() < 12) {
			return;
		}
		String color = getColor(getInt(props, 2));
		String style = (String)props.get(4);
		JSONObject o = setColor(init(wb, props), color, color)
				.put(ATTR_TYPE, "i-text")
				.put("text", props.get(1))
				.put("fontSize", props.get(3));
		if (style.indexOf("bold") > -1) {
			o.put("fontWeight", "bold");
		}
		if (style.indexOf("italic") > -1) {
			o.put("fontStyle", "inalic");
		}
		add(wb, o);
	}

	private static void processPath(Whiteboard wb, List<?> props) {
		if (props.size() < 13) {
			return;
		}
		String color = getColor(getInt(props, 4));
		JSONObject o = setColor(init(wb, props), color, null)
				.put(ATTR_TYPE, "path")
				.put(ATTR_STROKE, props.get(3));
		@SuppressWarnings("unchecked")
		List<List<?>> points = (List<List<?>>)props.get(1);
		JSONArray path = new JSONArray();
		for (List<?> point : points) {
			if (path.length() == 0) {
				path.put(new JSONArray(Arrays.asList("M", getInt(point, 1), getInt(point, 2))));
			} else if (path.length() == points.size() - 1) {
				path.put(new JSONArray(Arrays.asList("L", getInt(point, 3), getInt(point, 4))));
			} else {
				path.put(new JSONArray(Arrays.asList("Q"
						, getInt(point, 1), getInt(point, 2)
						, getInt(point, 3), getInt(point, 4))));
			}
		}
		add(wb, o.put("path", path).put(ATTR_OPACITY, props.get(5)));
	}

	private static void processLine(Whiteboard wb, List<?> props) {
		if (props.size() < 16) {
			return;
		}
		String color = getColor(getInt(props, 1));
		add(wb, setColor(init(wb, props), color, color)
				.put(ATTR_TYPE, "line")
				.put(ATTR_STROKE, props.get(2))
				.put(ATTR_OPACITY, props.get(3))
				.put("x1", props.get(4))
				.put("y1", props.get(5))
				.put("x2", props.get(6))
				.put("y2", props.get(7)));
	}

	private static int getInt(List<?> props, int idx) {
		Object o = props.get(idx);
		if (o instanceof Number) {
			return ((Number) o).intValue();
		} else if (o instanceof String) {
			return NumberUtils.toInt((String)o);
		}
		return 0;
	}

	private static JSONObject processRect(Whiteboard wb, List<?> props) {
		if (props.size() < 15) {
			return null;
		}
		return setColor(init(wb, props)
					, 1 == getInt(props, 4) ? getColor(getInt(props, 1)) : null
					, 1 == getInt(props, 5) ? getColor(getInt(props, 3)) : null)
				.put(ATTR_TYPE, "rect")
				.put(ATTR_STROKE, props.get(2))
				.put(ATTR_OPACITY, props.get(6));
	}

	private static void processEllipse(Whiteboard wb, List<?> props) {
		JSONObject o = processRect(wb, props);
		if (o != null) {
			o.put(ATTR_TYPE, "ellipse")
				.put("rx", o.getDouble("width") / 2)
				.put("ry", o.getDouble("height") / 2);
			add(wb, o);
		}
	}

	private static void processClipart(Whiteboard wb, List<?> props) {
		if (props.size() < 19) {
			return;
		}
		String src = (String)props.get(2);
		int idx = src.indexOf("cliparts");
		if (idx > -1) {
			src = String.format("./public/%s", src.substring(idx));
		}
		add(wb, init(wb, props)
				.put(ATTR_TYPE, TYPE_IMAGE)
				.put("omType", "Clipart")
				.put(PARAM__SRC, src)
				.put("angle", props.get(3)));
	}

	private static long getFileId(String src) {
		int idx1 = src.lastIndexOf('/'), idx2 = src.indexOf('?');
		if (idx1 < 0 || idx2 < 0) {
			return -1;
		}
		return toLong(src.substring(idx1 + 1, idx2), -1);
	}

	// will support only import from 3.2.x+
	private static void processImage(Whiteboard wb, List<?> props) {
		if (props.size() < 17) {
			return;
		}
		long fileId = getFileId((String)props.get(2));
		if (fileId < 0) {
			return;
		}
		add(wb, init(wb, props)
				.put(ATTR_TYPE, TYPE_IMAGE)
				.put(ATTR_FILE_TYPE, BaseFileItem.Type.Image.name())
				.put(ATTR_FILE_ID, fileId));
	}

	private static void processDoc(Whiteboard wb, List<?> props) {
		if (props.size() < 27) {
			return;
		}
		long fileId = getFileId((String)props.get(2));
		if (fileId < 0) {
			return;
		}
		add(wb, init(wb, props, false)
				.put(ATTR_TYPE, TYPE_IMAGE)
				.put(ATTR_FILE_TYPE, BaseFileItem.Type.Presentation.name())
				.put(ATTR_FILE_ID, fileId));
	}

	private static void processVid(Whiteboard wb, List<?> props) {
		if (props.size() < 14) {
			return;
		}
		add(wb, init(wb, props)
				.put(ATTR_TYPE, TYPE_IMAGE)
				.put(ATTR_FILE_TYPE, BaseFileItem.Type.Video.name())
				.put(ATTR_FILE_ID, props.get(1)));
	}

	public static Whiteboard convert(FileItem fi) {
		Whiteboard wb = new Whiteboard();
		wb.setWidth(0);
		wb.setHeight(0);
		Set<String> uids = new HashSet<>();
		List<?> wml = loadWmlFile(fi.getHash());
		for (Object wo : wml) {
			List<?> props = (List<?>)wo;
			if (!props.isEmpty()) {
				String uid = (String)props.get(props.size() - 1);
				if (uids.contains(uid)) {
					continue;
				}
				uids.add(uid);
				switch ((String)props.get(0)) {
					case "letter":
						processText(wb, props);
						break;
					case "paint":
						processPath(wb, props);
						break;
					case "line":
					case "uline":
						processLine(wb, props);
						break;
					case "rectangle":
					case "drawarrow": // will replace with rectangle
						add(wb, processRect(wb, props));
						break;
					case "ellipse":
						processEllipse(wb, props);
						break;
					case "clipart":
						processClipart(wb, props);
						break;
					case TYPE_IMAGE:
						processImage(wb, props);
						break;
					case "swf":
						processDoc(wb, props);
						break;
					case "flv":
						processVid(wb, props);
						break;
				}
			}
		}
		return wb;
	}

	public static List<?> loadWmlFile(String hash) {
		String name = OmFileHelper.getName(hash, EXTENSION_WML);
		File file = new File(OmFileHelper.getUploadWmlDir(), name);
		log.debug("filepathComplete: {}", file);

		XStream xstream = new XStream(new XppDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.addPermission(NoTypePermission.NONE);
		xstream.addPermission(NullPermission.NULL);
		xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
		xstream.allowTypeHierarchy(List.class);
		xstream.allowTypeHierarchy(String.class);
		try (InputStream is = new FileInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8))) {
			return (List<?>) xstream.fromXML(reader);
		} catch (Exception err) {
			log.error("loadWmlFile", err);
		}
		return new ArrayList<>();
	}
}
