'use strict';

/* mock db calls */
jest.mock('../../index.js');
const mainMod = require('../../index.js');

const profile = require('../../routes/profile.js');

const email = "test_email@gmail.com";

var profileInfo = {
	bio:"wow",
	displayName:"BFLHolmes",
	preferences:"I like food"
};

var getProfileInfoMocks = {
	collection: jest.fn().mockImplementation(() => getProfileInfoMocks),
	findOne: jest.fn().mockImplementation(() => profileInfo)
};

it('Queries database with correct params', async () => {
	mainMod.getDb.mockImplementation(() => getProfileInfoMocks);

	var actualOut = await profile.getProfileInfo(email);
	expect(actualOut).toEqual(profileInfo);

	expect(getProfileInfoMocks.findOne).toBeCalledWith(
		{email: email},
		{projection:
			{displayName: 1, bio: 1, preferences: 1}
		}
	);
});

it('getProfileInfo throws error when fail to connect to DB', async () => {
	mainMod.getDb.mockImplementation(() => null);

	const email = "test_email@gmail.com";
	expect(profile.getProfileInfo(email))
		.rejects.toEqual("Could not connect to DB");
});

it('getProfileInfo throws error when email is undefined', async () => {
	mainMod.getDb.mockImplementation(() => getProfileInfoMocks);

	expect(profile.getProfileInfo(undefined))
		.rejects.toEqual("Undefined email");
});

it('getProfileInfo throws error when email is empty', async () => {
	mainMod.getDb.mockImplementation(() => getProfileInfoMocks);

	expect(profile.getProfileInfo(''))
		.rejects.toEqual("Empty email address");
});

/**
 * Update profile info 
 */
var updateProfileInfoReq = {
	body: {
		email: "user@gmail.com",
		bio: "wow",
		displayName:"BFLHolmes",
		preferences:"I like food",
		radiusPreference: 1,
	}
};

var updateProfileInfoRes = {
	json: jest.fn().mockImplementation((input) => {}),
};

var updateProfileInfoMocks = {
	collection: jest.fn().mockImplementation(() => updateProfileInfoMocks),
	updateOne: jest.fn().mockImplementation(() => null)
};

it('updateProfileInfo updates with correct params', async () => {
	mainMod.getDb.mockImplementation(() => updateProfileInfoMocks);
	await profile.updateProfileInfo(updateProfileInfoReq, updateProfileInfoRes);
	expect(updateProfileInfoRes.json).toBeCalledWith({"updateProfileInfo": true});
});

it('updateProfileInfo throws error when fail to connect to DB', async () => {
	mainMod.getDb.mockImplementation(() => null);
	expect(profile.updateProfileInfo({}))
		.rejects.toEqual("Could not connect to DB");
});

it('updateProfileInfo throws error when request body is underfined', async () => {
	mainMod.getDb.mockImplementation(() => updateProfileInfoMocks);
	var updateProfileInfoReq = {
		email: "user@gmail.com",
		bio: "wow",
		displayName:"BFLHolmes",
		preferences:"I like food",
		radiusPreference: 1,
	};
	expect(profile.updateProfileInfo(updateProfileInfoReq))
		.rejects.toEqual("Request body is undefined");
});

it('updateProfileInfo throws error when email is empty or null', async () => {
	mainMod.getDb.mockImplementation(() => updateProfileInfoMocks);
	var updateProfileInfoReq = {
		body: {
			email: "",
			bio: "wow",
			displayName:"BFLHolmes",
			preferences:"I like food",
			radiusPreference: 1,
		}
	};
	expect(profile.updateProfileInfo(updateProfileInfoReq))
		.rejects.toEqual("Email is null or empty");

	updateProfileInfoReq = {
		body: {
			email: null,
			bio: "wow",
			displayName:"BFLHolmes",
			preferences:"I like food",
			radiusPreference: 1,
		}
	};

	expect(profile.updateProfileInfo(updateProfileInfoReq))
		.rejects.toEqual("Email is null or empty");
});

/**
 * getProfile
 */
var getProfileReq = {
	query: {
		email: email,
	}
};

var getProfileRes = {
	json: jest.fn().mockImplementation((input) => {}),
};

it('getProfile with correct params', async () => {
	mainMod.getDb.mockImplementation(() => getProfileInfoMocks);
	await profile.getProfile(getProfileReq, getProfileRes);
	expect(getProfileRes.json).toBeCalledWith(profileInfo);
});

it('getProfile throw error when fail to connect to DB', async () => {
	mainMod.getDb.mockImplementation(() => null);
	expect(profile.getProfile(getProfileReq, getProfileRes))
		.rejects.toEqual("Could not connect to DB");
});