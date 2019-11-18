'use strict';

jest.mock('../index.js');
const mainMod = require('../index.js');
const get = require('../routes/get.js');

const {MongoClient} = require("mongodb");
const mongoProdUri = "mongodb+srv://dbrui:cpen321@cluster0-mfvd7.azure.mongodb.net/admin?retryWrites=true&w=majority";

describe('getAllRequests', () => {
  let connection;
  let db;

  beforeAll(async () => {
    connection = await MongoClient.connect(mongoProdUri, {
      useNewUrlParser: true,
    });
    db = await connection.db("ingrediShareTest");

    const requests = db.collection("requests");
    const mockRequest = {name: "testReq", description:"one", lat:1, long:1, userId:"test@gmail.com"};
    await requests.insertOne(mockRequest);
  });

  afterAll(async () => {
    db.collection("requests").deleteOne({name: "testReq"}, function(err,obj){
        if(err){
            throw err;
        }
        console.log("deleted data inserted as test successfully")
    })
    await connection.close();
    await db.close();
  });

  it('Returns all requests as expected', async () => {
      mainMod.getDb.mockImplementation(() => db);
      const data = await get.getAllRequests();
      const jsonData = JSON.stringify(data);
      expect(jsonData).toContain("\"name\":\"testReq\",\"description\":\"one\",\"lat\":1,\"long\":1,\"userId\":\"test@gmail.com\"");
  });
});