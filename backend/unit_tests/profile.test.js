'use strict';

/* mock db calls */
jest.mock('../index.js');
const mainMod = require('../index.js');

const profile = require('../routes/profile.js');

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
	const email = "test_email@gmail.com";

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