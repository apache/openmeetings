/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
import { OmWbText } from './wb-tool-text';
import { ToolUtil } from './wb-tool-util';

import * as fabric from 'fabric';

export class Textbox extends OmWbText {
	constructor(wb, s, sBtn) {
		super(wb, s, sBtn);
		this.omType = 'textbox';
	}

	createTextObj(canvas, pointer) {
		return new fabric.Textbox('', {
			left: pointer.x
			, top: pointer.y
			, padding: 7
			, fill: this.fill.enabled ? this.fill.color : ToolUtil.noColor
			, stroke: this.stroke.enabled ? this.stroke.color : ToolUtil.noColor
			//, strokeWidth: this.stroke.width
			, fontSize: this.stroke.width
			, omType: this.omType
			, fontFamily: this.fontFamily
			, opacity: this.opacity
			, breakWords: true
			, width: canvas.width / 4
			, lockScalingX: false
			, lockScalingY: true
		});
	}

	_doubleClick(e) {
		const ao = e.target;
		if (!!ao && this.omType === ao.omType) {
			this.obj = ao;
			this.obj.enterEditing();
			WbArea.removeDeleteHandler();
		}
	}

	_onMouseDown() {
		WbArea.addDeleteHandler();
	}

	_onActivate() {
		WbArea.addDeleteHandler();
	}

	_onDeactivate() {
		WbArea.addDeleteHandler();
	}
};
