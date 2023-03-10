const colors = ['#ff0000', '#ffa700', '#fff400', '#a3ff00', '#2cba00'];

const scale = [0, 0.2, 0.4, 0.6, 0.8];

export const legendItems = [
	{title: '< 20%', color: colors[0], textColor: 'black'},
	{title: '20%-40%', color: colors[1], textColor: 'black'},
	{title: '40%-60%', color: colors[2], textColor: 'black'},
	{title: '60%-80%', color: colors[3], textColor: 'black'},
	{title: '> 80%', color: colors[4], textColor: 'black'},
];

export const getColor = (val) => {
	for (let i = 1; i < scale.length; i++) {
		if (val < scale[i]) {
			return colors[i - 1];
		}
	}
	return colors[colors.length - 1];
};

export const separateNumberWithCommas = (val) => {
	// remove sign if negative
	var sign = 1;
	if (val < 0) {
		sign = -1;
		val = -val;
	}
	// trim the number decimal point if it exists
	let num = val.toString().includes('.') ? val.toString().split('.')[0] : val.toString();
	let len = num.toString().length;
	let result = '';
	let count = 1;

	for (let i = len - 1; i >= 0; i--) {
		result = num.toString()[i] + result;
		if (count % 3 === 0 && count !== 0 && i !== 0) {
			result = ',' + result;
		}
		count++;
	}

	// add number after decimal point
	if (val.toString().includes('.')) {
		result = result + '.' + val.toString().split('.')[1];
	}
	// return result with - sign if negative
	return sign < 0 ? '-' + result : result;
};

export const swapLatLng = (coordinates) => {
	coordinates[0] = coordinates.splice(1, 1, coordinates[0])[0];
	return coordinates;
};

export const deliveryStatus = {
	IN_TIME: {color: 'text-green-500', text: 'In time'},
	DELAYED: {color: 'text-orange-500', text: 'Delayed'},
	DELIVERED: {color: 'text-gray-500', text: 'Delivered'},
};

export const millisecondsToDate = (milliseconds) => {
	const date = new Date(milliseconds);
	return date.toLocaleDateString('en-US', {
		year: 'numeric',
		month: 'short',
		day: 'numeric',
	});
};

// convert milliseconds to YYYY-MM-DD
export const millisecondsToYYYYMMDD = (milliseconds) => {
	var date = new Date(milliseconds); // Date 2011-05-09T06:08:45.178Z
	var year = date.getFullYear();
	var month = ('0' + (date.getMonth() + 1)).slice(-2);
	var day = ('0' + date.getDate()).slice(-2);

	return `${year}-${month}-${day}`; // 2011-05-09
};

export const daysBetween = (date1, date2) => {
	const oneDay = 24 * 60 * 60 * 1000;

	const newDate1 = new Date(date1);
	newDate1.setHours(0, 0, 0, 0);

	const newDate2 = new Date(date2);
	newDate2.setHours(0, 0, 0, 0);

	const differenceInMs = Math.abs(newDate2 - newDate1);

	return Math.round(differenceInMs / oneDay);
};
