'use strict';

jest.mock('../index.js');
const mainMod = require('../index.js');
const profile = require('../routes/profile.js');

const {MongoClient} = require("mongodb");
const mongoProdUri = "mongodb+srv://dbrui:cpen321@cluster0-mfvd7.azure.mongodb.net/admin?retryWrites=true&w=majority";

describe('getProfileInfo', () => {
    let connection;
    let db;
    const userEmail = 'user@gmail.com';
    const testUser = {
        email: userEmail, 
        radiusPreference: 1,
        displayName: "testUser",
        bio: "my bio",
        preferences: "my preferences"
    };
  
    beforeAll(async () => {
      connection = await MongoClient.connect(mongoProdUri, {
        useNewUrlParser: true,
      });
      db = await connection.db("ingrediShareTest");
  
      const users = db.collection("users");
      await users.insertOne(testUser);
    });
  
    afterAll(async () => {
      db.collection("users").deleteOne(testUser, function(err,obj){
        if(err){
            throw err;
        }
        console.log("deleted user inserted as test successfully")
    })
      await connection.close();
      await db.close();
    });
  
    it('Returns user as expected', async () => {
        mainMod.getDb.mockImplementation(() => db);
        const data = await profile.getProfileInfo(userEmail);
        const jsonData = JSON.stringify(data);
        expect(jsonData).toContain("\"displayName\":\"testUser\",\"bio\":\"my bio\",\"preferences\":\"my preferences\"}");
    });
  });