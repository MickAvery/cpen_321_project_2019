'use strict';

/* mock db calls */
jest.mock('../../index.js');
const mainMod = require('../../index.js');

const get = require('../../routes/get.js');

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

it('throws error when getAllRequestsFromLatLong fail to connect to DB ', async () => {
	mainMod.getDb.mockImplementation(() => null);
	expect(get.getAllRequestsFromLatLong({email:"test_email@google.com"}))
		.rejects.toEqual("Could not connect to DB");
});

it('throws error when getAllRequestsFromLatLong req json is missing email field ', async () => {
	mainMod.getDb.mockImplementation(() => getAllReqLatLongMocks);
	expect(get.getAllRequestsFromLatLong({invalid_param:"invalid@gmail.com"}))
		.rejects.toEqual("Req json is missing email field.");
});

it('getAllRequestsFromLatLong invalid param: latitude', async () => {
	mainMod.getDb.mockImplementation(() => getAllReqLatLongMocks);
	expect(get.getAllRequestsFromLatLong({
		email:"test_email@google.com",
		lat: -100
	}))
		.rejects.toEqual("Invalid latitude");
});

it('getAllRequestsFromLatLong invalid param: longitude', async () => {
	mainMod.getDb.mockImplementation(() => getAllReqLatLongMocks);
	expect(get.getAllRequestsFromLatLong({
		email:"test_email@google.com",
		lat: 20,
		long: 270
	}))
		.rejects.toEqual("Invalid longitude");
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

/**
 * handleResponse() tests
 */
var resMockWithCreateRequestResponseTrue = {
	json: jest.fn().mockImplementation((input) => {
		expect(input.createRequestResponse).toBe(true);
	})
}

var resMockWithCreateRequestResponseFalse = {
	json: jest.fn().mockImplementation((input) => {
		expect(input.createRequestResponse).toBe(false);
	})
}

it('HandleResponse returns createRequestResponse = false when API has error', async () => {
	expect(get.handleResponse(true, resMockWithCreateRequestResponseFalse));
});

it('HandleResponse returns createRequestResponse = true when API has error', async () => {
	expect(get.handleResponse(false, resMockWithCreateRequestResponseTrue));
});

/**
 * createRequest
 */
var request = {
	body: {
		userId: "test_email@gmail.com",
		name: "test body",
		description: "test description",
		lat: "5",
		long: "5",
		type: "Request type"
	}
}

var invalidRequest = {
	body: {
		userId: "",
		name: "test body",
		description: "test description",
		lat: "5",
		long: "5",
		type: "Request type"
	}
}

var end = {
	end: jest.fn().mockImplementation(() => null)
};

var createRequestMocks = {
	collection: jest.fn().mockImplementation(() => createRequestMocks),
	insertOne: jest.fn().mockImplementation(() => null),
}

var resMockWithErrorCode500 = {
	status: jest.fn().mockImplementation(() => end)
}

it('fails with error code 500 when could not connect to DB', async () => {
	mainMod.getDb.mockImplementation(() => null);
    await get.createRequest(request, resMockWithErrorCode500);
});

it('succeeds with createRequestResponse = false when the request is invalid', async () => {
	mainMod.getDb.mockImplementation(() => createRequestMocks);
    await get.createRequest(invalidRequest, resMockWithCreateRequestResponseFalse);
});