/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function onOmGotoClick() {
	const gotoBtn = document.querySelector('#calendar .fc-gotoBtn-button');
	if (gotoBtn.datetimepicker === undefined) {
		gotoBtn.datetimepicker = new tempusDominus.TempusDominus(gotoBtn, {
			display: {
				icons: {
					time: 'fas fa-clock'
					, date: 'fas fa-calendar'
					, up: 'fas fa-arrow-up'
					, down: 'fas fa-arrow-down'
					, previous: 'fas fa-chevron-left'
					, next: 'fas fa-chevron-right'
					, today: 'fas fa-calendar-check'
					, clear: 'fas fa-trash'
					, close: 'fas fa-times'
				}
				, buttons: {
					today: true
					, clear: true
					, close: true
				}
			}
			, localization: {
				locale: document.querySelector('#calendar').calendar.getOption('locale')
				, format: 'LLL'
			}
		});
		gotoBtn.datetimepicker.subscribe(tempusDominus.Namespace.events.hide, (e) => {
			document.querySelector('#calendar').calendar.gotoDate(e.date.startOf('date'));
		});
		gotoBtn.datetimepicker.show();
	}
}
