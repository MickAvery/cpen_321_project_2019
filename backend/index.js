const http = require('http');

const server = http.createServer((request, response, next) => {
    try {
        const listingUrl = "https://www.airbnb.com/rooms/10006546";
    getData(listingUrl).then((result) => {
        response.writeHead(200, {"Content-Type": "text/plain"});
        response.end("Hello World! The Airbnb description for the listing " + listingUrl + " is " + result.summary); 
    });
    } catch (err) {
        next(err)
    }
    
    // response.writeHead(200, {"Content-Type": "text/plain"});
    // response.end("Hello World!");
});

async function getData(listingUrl) {
    const MongoClient = require('mongodb').MongoClient;
    const uri = "mongodb+srv://dbrui:cpen321@cluster0-mfvd7.azure.mongodb.net/admin?retryWrites=true&w=majority";
    const db = await MongoClient.connect(uri);
    const dbo = db.db("sample_airbnb");
    const result = await dbo.collection("listingsAndReviews").findOne({ listing_url: listingUrl });
    return result;
}

const port = process.env.PORT || 1337;
server.listen(port);

console.log("Server running at http://localhost:%d", port);