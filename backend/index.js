var express = require('express');
var app     = express();
var mongo   = require('mongodb');

const {OAuth2Client} = require('google-auth-library');
const SERVER_CLIENT_ID = '127621605968-j54jl9efu5b5jfhoo5bub65vsokohp5r.apps.googleusercontent.com';
const APP_CLIENT_ID = '127621605968-bco5cfpv64kpjs5jb06pcum78649jese.apps.googleusercontent.com';
const client = new OAuth2Client(SERVER_CLIENT_ID); /* TODO: set this */

var bodyParser = require('body-parser');
app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies

/**
 * Setup DB
 **/
var mongoClient = mongo.MongoClient;
const mongoLocalUri = "mongodb://localhost:27017/";
const mongoProdUri = "mongodb+srv://dbrui:cpen321@cluster0-mfvd7.azure.mongodb.net/admin?retryWrites=true&w=majority";

/* TODO: how to either connect to local DB or cloud DB? */
mongoClient.connect((mongoLocalUri), function(err, db) {
    if (err) throw err;
    var dbo = db.db("mydb");
    dbo.createCollection("customers", function(err, res) {
        if (err) throw err;
        console.log("Collection created!");
        db.close();
    });
});

/**
 * Setup server
 */
const port = process.env.PORT || 1337;
var server = app.listen(port, function() {
    var host = server.address().address;
    var port = server.address().port;
    console.log("Example app listening at http://%s:%s", host, port);
});

/**
 * RESTful services
 **/
app.get('/getAllRequests', function(req, res) {
    console.log("Test");
    try {
        getAllRequests().then((result) => {
            res.writeHead(200, {"Content-Type": "text/plain"});
            var str = "";
            result.forEach(function (item) {
                str += JSON.stringify(item);
            });
            res.end(str);
        });
    } catch (err) {
    }
});

app.get('/', function(req, res) {
    res.send("Hello world!");
});

app.post('/tokensignin', function(req, res) {
    console.log("/tokensignin POST");
    var token = req.body.idToken;

    /* TODO: token error checking */

    try {
        verifyToken(token).then((result) => {
            // console.log(result);
            res.json({"user_exists" : false});
        });
    } catch(err) {

    }
});

app.get('/yada', function(req, res) {
    console.log("got something");
});

async function verifyToken(token) {
    const ticket = await client.verifyIdToken({
        idToken : token,
        audience: SERVER_CLIENT_ID
    });
    // console.log(ticket);
    const payload = ticket.getPayload();
    const userid = payload['sub'];
    const email = payload['email'];
    
    return email;
}

async function getAllRequests() {
    const db = await mongoClient.connect(mongoLocalUri);
    const dbo = db.db("ingrediShare");
    const result = await dbo.collection("requests").find({}).toArray();

    return result;
}