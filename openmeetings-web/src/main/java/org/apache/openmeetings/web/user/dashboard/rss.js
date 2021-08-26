/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function addItem(feeds, item) {
	feeds.append('<h3>' + item.title + '</h3>')
		.append($('<div></div>').append(item.content));
}
function displayRss(entries) {
	$('#feedcontainer').html('');
	$('#feedcontainer').append('<div id="feeds"></div>');
	const feeds = $('#feeds');
	for (let i = 0; i < entries.length; ++i) {
		const headId = `om-rss-heading${i}`
			, bodyId = `om-rss-content${i}`
			, markup = OmUtil.tmpl('#rss-item-template');
		markup.find('.card-header').attr('id', headId);
		markup.find('button').attr('data-bs-target', `#${bodyId}`).attr('aria-controls', bodyId)
			.append(entries[i].title);
		markup.find('.collapse').attr('id', bodyId).attr('aria-labelledby', headId);
		markup.find('.card-body').html(entries[i].content);
		feeds.append(markup);
	}
}
