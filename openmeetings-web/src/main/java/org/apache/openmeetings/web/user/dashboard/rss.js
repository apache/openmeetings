/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var MAX_ITEM_COUNT = 5;
function getRssItems(url, next) {
	//TODO items need to be merged and sorted by date
	$.ajax({
		url: url
		, type: 'GET'
		, crossDomain: true
		, dataType: 'xml'
		, cache: false
		, success: function(data) {
			const $xml = $(data);
			let counter = MAX_ITEM_COUNT;
			$xml.find("item").each(function() {
				const $this = $(this), item = {
					title : $this.find("title").text(),
					link : $this.find("link").text(),
					description : $this.find("description").text(),
					pubDate : $this.find("pubDate").text(),
					author : $this.find("author").text()
				}
				addItem(item);
				if (--counter < 0) {
					return false;
				}
			});
			counter = MAX_ITEM_COUNT;
			$xml.find("entry").each(function() {
				const $this = $(this), item = {
					title : $this.find("title").text(),
					link : $this.find("link").text(),
					description : $this.find("content"),
					pubDate : $this.find("published").text(),
					author : $this.find("author").text()
				}
				addItem(item);
				if (--counter < 0) {
					return false;
				}
			});
			if (next) {
				getRssItems(next, null)
			} else {
				$('#feeds').accordion({
					collapsible: true
					, active: false
					, header: 'h3'
					, heightStyle: "content"
					});
			}
		}
	});
}
function addItem(item) {
	//TODO need to be checked
	$('#feeds').append('<h3>' + item.title + '</h3>')
		.append($('<div></div>').append(item.description));
}
function loadRssTab(url1, url2) {
	$('#feedcontainer').html('');
	$('#feedcontainer').append('<div id="feeds"></div>');
	getRssItems(url1, url2);
}
