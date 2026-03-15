export function timeFormat(data) {
	if (typeof data === 'string') {
		data = new Date(data.replace(/-/g, '/')).getTime()
	} else {
		if(!data){
			return
		}
		if (data.toString().length === 10) {
			data = new Date(data * 1000).getTime()
		} else {
			data = new Date(data).getTime()
		}
	}
	let result;
	let timePublish = new Date(data);
	let timeNow = new Date();
	let minute = 1000 * 60;
	let hour = minute * 60;
	let day = hour * 24;
	let month = day * 30;
	let diffValue = timeNow - timePublish;
	let diffMonth = diffValue / month;
	let diffWeek = diffValue / (7 * day);
	let diffDay = diffValue / day;
	let diffHour = diffValue / hour;
	let diffMinute = diffValue / minute;
	
	if (diffValue < minute) {
		result = "刚刚";
	} else if (diffMonth > 1) {
		// 超过1个月，直接显示年月日格式
		let year = timePublish.getFullYear();
		let month = (timePublish.getMonth() + 1).toString().padStart(2, '0');
		let day = timePublish.getDate().toString().padStart(2, '0');
		result = `${year}-${month}-${day}`;
	} else if (diffWeek > 1) {
		result = parseInt(diffWeek) + "周前";
	} else if (diffDay > 1) {
		result = parseInt(diffDay) + "天前";
	} else if (diffHour > 1) {
		result = parseInt(diffHour) + "小时前";
	} else if (diffMinute > 1) {
		result = parseInt(diffMinute) + "分钟前";
	} else {
		result = "刚刚";
	}
	return result;
}

export function numberFormat(number) {
	if (number > 9999 && number <= 99999) {
		return (number / 10000).toFixed(1) + 'w';
	} else if (number > 99999 && number <= 999999) {
		return (number / 10000).toFixed(1) + '0w';
	} else if (number >= 999999) {
		return "100w+";
	}
	return number;
}
