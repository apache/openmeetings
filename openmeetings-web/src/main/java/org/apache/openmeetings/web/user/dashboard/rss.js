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
		addItem(feeds, entries[i]);
	}
	feeds.accordion({
		collapsible: true
		, active: false
		, header: 'h3'
		, heightStyle: "content"
		});
}
