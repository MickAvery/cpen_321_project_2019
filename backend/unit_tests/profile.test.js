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