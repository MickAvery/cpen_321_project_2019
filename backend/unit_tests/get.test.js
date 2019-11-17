'use strict';

jest.mock('../index.js');

const get = require('../routes/get.js');

it('works with async/await', async () => {
    const data = await get.getAllRequests();
    expect(data).toEqual('Mark');
});
  