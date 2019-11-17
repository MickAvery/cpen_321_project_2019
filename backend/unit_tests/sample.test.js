test("This is a sample test", () => {
    expect(2 + 2).toBe(4);
});

describe("Sample Test", () => {
    it("should test that true === true", () => {
      expect(true).toBe(true)
    })
})

const {MongoClient} = require("mongodb");
const mongoProdUri = "mongodb+srv://dbrui:cpen321@cluster0-mfvd7.azure.mongodb.net/admin?retryWrites=true&w=majority";

describe('insert', () => {
  let connection;
  let db;

  beforeAll(async () => {
    connection = await MongoClient.connect(mongoProdUri, {
      useNewUrlParser: true,
    });
    db = await connection.db("ingrediShareTest");
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

  it("should insert a doc into collection", async () => {
    const requests = db.collection("requests");

    const mockRequest = {name: "testReq", description:"one", lat:1, long:1, userId:"test@gmail.com"};
    await requests.insertOne(mockRequest);

    const insertedRequest = await requests.findOne({name: "testReq"});
    expect(insertedRequest).toEqual(mockRequest);
  });
});