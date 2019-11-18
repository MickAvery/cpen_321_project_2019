'use strict';

/* mock db calls */
jest.mock('../index.js');
const mainMod = require('../index.js');

const get = require('../routes/get.js');

/**
 * getAllRequests() tests
 */

var requestArray = [
	{
		name:"test",
		description:"test",
		lat:1,
		long:1,
		userId:"test@gmail.com",
		type:"Request Ingredient"
	},
	{
		name:"Extra cookies",
		description:"at wood library",
		lat:50,
		long:50,
		userId:"test@gmail.com",
		type:"Offer Ingredient"
	},
	{
		name: "cheese",
		description: "marble",
		lat: 1,
		long: 1,
		userId :"g@f.com",
		type :"Request Ingredient"
	}
];

var getAllReqMocks = {
	collection: jest.fn().mockImplementation(() => getAllReqMocks),
	find: jest.fn().mockImplementation(() => getAllReqMocks),
	toArray: jest.fn().mockImplementation(() => requestArray)
}

it('Returns all requests as expected', async () => {
	mainMod.getDb.mockImplementation(() => getAllReqMocks);

    const data = await get.getAllRequests();
    expect(data).toEqual(requestArray);
});

/**
 * getAllRequestsFromLatLong() tests
 */

var requestArray = [
	{
		name:"test",
		description:"test",
		lat:1,
		long:1,
		userId:"test@gmail.com",
		type:"Request Ingredient"
	},
	{
		name:"Extra cookies",
		description:"at wood library",
		lat:50,
		long:50,
		userId:"test@gmail.com",
		type:"Offer Ingredient"
	},
	{
		name: "cheese",
		description: "marble",
		lat: 1,
		long: 1,
		userId :"g@f.com",
		type :"Request Ingredient"
	}
];

var getAllReqLatLongMocks = {
	collection: jest.fn().mockImplementation(() => getAllReqLatLongMocks),
	findOne: jest.fn().mockImplementation(() => getAllReqLatLongMocks),
	find: jest.fn().mockImplementation(() => getAllReqLatLongMocks),
	toArray: jest.fn().mockImplementation(() => requestArray)
}

it('Returns all requests with Lat+Long as expected', async () => {
	mainMod.getDb.mockImplementation(() => getAllReqLatLongMocks);

    var data = await get.getAllRequestsFromLatLong({email:"test_email@google.com"});
    expect(data).toEqual(requestArray);
});

/**
 * requestIsValid() tests
 */

var request = {
    userId: "test_email@gmail.com",
    name: "test body",
    description: "test description",
    lat: "5",
    long: "5",
    type: "Request type"
}

it('Invalidates an incomplete request', async () => {
	for(var key in request) {
		var tempReq = Object.assign({}, request); /* copy request json */
		tempReq[key] = null;
		expect(get.requestIsValid(tempReq)).toBe(false);
	}
});

it('Validates a complete request', async () => {
	expect(get.requestIsValid(request)).toBe(true);
});