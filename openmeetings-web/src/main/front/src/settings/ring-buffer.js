/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
module.exports = class RingBuffer {
	constructor(length) {
		const buffer = [];
		let pos = 0;

		this.get = (key) => {
			return buffer[key];
		};
		this.push = (item) => {
			buffer[pos] = item;
			pos = (pos + 1) % length;
		};
		this.min = () => {
			return Math.min.apply(Math, buffer);
		}
	}
};
