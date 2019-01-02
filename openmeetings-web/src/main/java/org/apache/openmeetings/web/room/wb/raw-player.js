/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Player = (function() {
	const player = {}, mainColor = '#ff6600', rad = 20;
	function _sendStatus(g, _paused, _pos) {
		g.status.paused = _paused;
		g.status.pos = _pos;
		wbAction('videoStatus', JSON.stringify({
			wbId: g.canvas.wbId
			, uid: g.uid
			, status: {
				paused: _paused
				, pos: _pos
			}
		}));
	}

	player.create = function(canvas, _o, wb) {
		const vid = $('<video>').hide().attr('class', 'wb-video slide-' + canvas.slide).attr('id', 'wb-video-' + _o.uid)
			.attr("width", _o.width).attr("height", _o.height)
			.append($('<source>').attr('type', 'video/mp4').attr('src', _o._src));
		$('#wb-tab-' + canvas.wbId).append(vid);
		fabric.Image.fromURL(_o._poster, function(poster) {
			poster.scaleX = poster.scaleY = _o.width / poster.getOriginalSize().width;
			const video = new fabric.Image(vid[0], {visible: false, objectCaching: false});
			vid[0].onseeked = function() {
				canvas.requestRenderAll();
			};
			if (typeof(_o.status) === 'undefined') {
				_o.status = {paused: true};
			}
			let playable = false;
			const trg = new fabric.Triangle({
				left: 2.7 * rad
				, top: _o.height - 2.5 * rad
				, visible: _o.status.paused
				, angle: 90
				, width: rad
				, height: rad
				, stroke: mainColor
				, fill: mainColor
			});
			const rectPause1 = new fabric.Rect({
				left: 1.6 * rad
				, top: _o.height - 2.5 * rad
				, visible: !_o.status.paused
				, width: rad / 3
				, height: rad
				, stroke: mainColor
				, fill: mainColor
			});
			const rectPause2 = new fabric.Rect({
				left: 2.1 * rad
				, top: _o.height - 2.5 * rad
				, visible: !_o.status.paused
				, width: rad / 3
				, height: rad
				, stroke: mainColor
				, fill: mainColor
			});
			const play = new fabric.Group([
					new fabric.Circle({
						left: rad
						, top: _o.height - 3 * rad
						, radius: rad
						, stroke: mainColor
						, strokeWidth: 2
						, fill: null
					})
					, trg, rectPause1, rectPause2]
				, {
					objectCaching: false
					, visible: false
				});
			const cProgress = new fabric.Rect({
				left: 3.5 * rad
				, top: _o.height - 1.5 * rad
				, visible: false
				, width: _o.width - 5 * rad
				, height: rad / 2
				, stroke: mainColor
				, fill: null
				, rx: 5
				, ry: 5
			});
			const isDone = function() {
				return video.getElement().currentTime === video.getElement().duration;
			};
			const updateProgress = function() {
				progress.set('width', (video.getElement().currentTime * cProgress.width) / video.getElement().duration);
				canvas.requestRenderAll();
			};
			const progress = new fabric.Rect({
				left: 3.5 * rad
				, top: _o.height - 1.5 * rad
				, visible: false
				, width: 0
				, height: rad / 2
				, stroke: mainColor
				, fill: mainColor
				, rx: 5
				, ry: 5
			});
			let request;

			const opts = $.extend({
				subTargetCheck: true
				, objectCaching: false
				, omType: 'Video'
				, selectable: canvas.selection
			}, ToolUtil.filter(_o, ['fileId', 'fileType', 'slide', 'uid', '_poster', '_src', 'width', 'height', 'status']));
			const group = new fabric.Group([video, poster, play, progress, cProgress], opts);

			const updateControls = function() {
				video.visible = true;
				poster.visible = false;

				trg.visible = group.status.paused;
				rectPause1.visible = !group.status.paused;
				rectPause2.visible = !group.status.paused;
				canvas.requestRenderAll();
			};
			const render = function () {
				if (isDone()) {
					_sendStatus(group, true, video.getElement().duration);
					updateControls();
				}
				updateProgress();
				if (group.status.paused) {
					cancelAnimationFrame(request);
					canvas.requestRenderAll();
				} else {
					request = fabric.util.requestAnimFrame(render);
				}
			};
			cProgress.on({
				'mousedown': function (evt) {
					const _ptr = canvas.getPointer(evt.e, true)
						, ptr = canvas._normalizePointer(group, _ptr)
						, l = ptr.x - cProgress.aCoords.bl.x + group.width / 2;
					_sendStatus(group, group.status.paused, l * video.getElement().duration / cProgress.width)
				}
			});
			play.on({
				/*
				 * https://github.com/kangax/fabric.js/issues/4115
				 *
				'mouseover': function() {
					circle1.set({strokeWidth: 4});
					canvas.requestRenderAll();
				}
				, 'mouseout': function() {
					circle1.set({
						left: pos.left
						, top: pos.top
						, strokeWidth: 2
					});
					canvas.requestRenderAll();
				}
				, */'mousedown': function () {
					play.set({
						left: pos.left + 3
						, top: pos.top + 3
					});
					canvas.requestRenderAll();
				}
				, 'mouseup': function () {
					play.set({
						left: pos.left
						, top: pos.top
					});
					if (isDone()) {
						video.getElement().currentTime = 0;
					}
					_sendStatus(group, !group.status.paused, video.getElement().currentTime)
					updateControls();
				}
			});
			group.on({
				'mouseover': function() {
					play.visible = playable;
					cProgress.visible = playable;
					progress.visible = playable;
					canvas.requestRenderAll();
				}
				, 'mouseout': function() {
					play.visible = false;
					cProgress.visible = false;
					progress.visible = false;
					canvas.requestRenderAll();
				}
			});
			group.setPlayable = function(_r) {
				playable = _r !== NONE;
			};
			group.videoStatus = function(_status) {
				group.status = _status;
				updateControls();
				video.getElement().currentTime = group.status.pos;
				updateProgress();
				if (group.status.paused) {
					video.getElement().pause();
				} else {
					video.getElement().play();
					fabric.util.requestAnimFrame(render);
				}
			}
			group.setPlayable(wb.getRole());
			canvas.add(group);
			canvas.requestRenderAll();
			player.modify(group, _o);

			const pos = {left: play.left, top: play.top};
		});
	};
	player.modify = function(g, _o) {
		const opts = $.extend({
			angle: 0
			, left: 10
			, scaleX: 1
			, scaleY: 1
			, top: 10
		}, ToolUtil.filter(_o, ['angle', 'left', 'scaleX', 'scaleY', 'top']));
		g.set(opts).setCoords();
		g.canvas.requestRenderAll();
	};
	return player;
})();
