/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
(function(i,s,o,g,r,a,m){
	i['GoogleAnalyticsObject']=r;
	i[r]=i[r]||function(){
		(i[r].q=i[r].q||[]).push(arguments)
	},i[r].l=1*new Date();
	a=s.createElement(o),m=s.getElementsByTagName(o)[0];
	a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
function initGA(code) {
	ga('create', code, 'auto');
}
function init() {
	ga('send', 'pageview');
}
function initHash() {
	ga('send', 'pageview', window.location.hash);

	$(window).bind('hashchange', function() {
		ga('send', 'pageview', window.location.hash);
	});
}
