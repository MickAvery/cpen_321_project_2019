var express = require('express');
var app     = express();
var mongo   = require('mongodb');
var gcm     = require('node-gcm');
var bcrypt  = require('bcrypt');

/*********************************************************************
 * MODULE SETUP
 *********************************************************************/
const azureServerURL = "https://ingredishare-backend.azurewebsites.net"
const localServerURL = "http://localhost:1337"
/**
 * Google oath2.0
 **/
const {OAuth2Client} = require('google-auth-library');
const SERVER_CLIENT_ID = '127621605968-j54jl9efu5b5jfhoo5bub65vsokohp5r.apps.googleusercontent.com';
const APP_CLIENT_ID = '127621605968-bco5cfpv64kpjs5jb06pcum78649jese.apps.googleusercontent.com';
const client = new OAuth2Client(SERVER_CLIENT_ID);

/**
 * Firebase Cloud Messaging
 **/
const FCM_API_KEY = 'AAAAHbbXKlA:APA91bFeV7nuPtdUEYgQipthO5o1nCvK-hh8Tuaaz6X_ygYiH7GDxkxP5DCE_p5dfTz1o-IRFiAomjdk1OEGoQofaW5WgOV2XoxEwN7F7zOGqangZ7y5a6-YR30Qw-3VK_fSUgTlJwbP';
var sender = new gcm.Sender(FCM_API_KEY);

/**
 * Express JSON parser
 **/
var bodyParser = require('body-parser');
app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies

/**
 * Setup MongoDB
 **/
var mongoClient = mongo.MongoClient;
const mongoLocalUri = "mongodb://localhost:27017/";
const mongoProdUri = "mongodb+srv://dbrui:cpen321@cluster0-mfvd7.azure.mongodb.net/admin?retryWrites=true&w=majority";
var dbObj;

/* <-- Connection for PROD. COMMENT THIS SECTION IF CONNECTING TO LOCAL [START HERE]*/
mongoClient.connect((mongoProdUri), function(err, db) {
    if(err) throw err;
    dbObj = db.db("ingrediShare");
    dbIngrediShare = db.db("ingrediShare");
    console.log("connected to MongoDB!");
    // dbObj.createCollection("customers", function(err, res) {
        // if (err) throw err;
        // console.log("MongoDB : Collection created!");
        // db.close();
    // });
});
/*Connection for PROD [END HERE] --> */

/* <-- Connection for LOCAL [START HERE] */
// mongoClient.connect((mongoLocalUri), function(err, db) {
//     if (err) throw err;
//     dbObj = db.db("mydb");
//     dbObj.createCollection("customers", function(err, res) {
//         if (err) throw err;
//         console.log("Collection created!");
//         // db.close();
//     });
//     dbIngrediShare = db.db("ingrediShare");
// });
/*Connection for LOCAL [END HERE] --> */

/**
 * Setup server
 */
const port = process.env.PORT || 1337;
var server = app.listen(port, function() {
    var host = server.address().address;
    var port = server.address().port;
    console.log("Example app listening at http://%s:%s", host, port);
});

/*********************************************************************
 * RESTFUL SERVICES
 *********************************************************************/

const routerGet = require('../backend/routes/get')
const routerProfile = require('../backend/routes/profile')
const url = require('url');
app.use(routerGet)
app.use(routerProfile)

app.post('/tokensignin', function(req, res) {
    var token = req.body.id_token;

    /* TODO: token error checking */

    try {
        verifyToken(token).then((user_email) => {
            var query = dbObj.collection("users").find({email:user_email}).toArray(function(err, result) {
                if(err) throw err;

                if(typeof result !== 'undefined' && result.length > 0) {
                    res.json({"pre_existing_user" : true});
                } else {
                    res.json({"pre_existing_user" : false});

                    /* TODO: save to db */
                    var newUser = {email : user_email};
                    dbObj.collection("users").insertOne(newUser, function(err, res) {
                        if(err) throw err;

                        console.log("Created new user!");
                    });
                }
            });
        });
    } catch(err) {

    }
});

app.put('/saveFcmToken', function(req, res) {
    console.log("/saveFcmToken PUT");

    var email = req.body.email;
    var tok = req.body.token;

    var query = {email : email};
    var obj = {fcm_tok : tok};
    var newVals = { $set : obj };

    var query = dbObj.collection("users").updateOne(query, newVals, function(err, res) {
        if(err) throw err;
        console.log("update success");
    });

    res.json({"dummy": "dummy"}); /* TODO: figure out how Volley on frontend can accept empty responses */
});

app.post('/fbSignIn', function(req, res) {
    console.log('/fbSignIn POST');

    var user_email = req.body.email;

    var query = dbObj.collection("users").find({email:user_email}).toArray(function(err, result) {
        if(err) throw err;

        if(typeof result !== 'undefined' && result.length > 0) {
            console.log('/fbSignIn POST : user exists');

            res.json({"pre_existing_user" : true});
        } else {
            var newUser = {email : user_email};
            dbObj.collection("users").insertOne(newUser, function(err, db_res) {
                if(err) throw err;

                console.log('/fbSignIn POST : user created');

                res.json({"pre_existing_user" : false});
            });
        }
    });
});

app.get('/userPassLogIn', function(req, res) {
    console.log('/userPassSignIn GET');

    var user_email = req.query.email;
    var password = req.query.password;
    console.log(user_email);
    console.log(password);

    /* verify that user exists in database */
    var query = dbObj.collection("users").find({email:user_email}).toArray(function(err, result) {
        if(err) throw err;

        if(typeof result !== 'undefined' && result.length > 0) {
            /* pre-existing user, verify password */
            console.log('/userPassLogIn GET : user exists, verify password');

            var hash = result[0].password;

            bcrypt.compare(password, hash, function(err, hash_result) {
                if(err) throw err;

                var ret;

                if(hash_result) {
                    /* Passwords match */
                    console.log('/userPassLogIn GET success : password correct');

                    ret = {"success" : true};
                } else {
                    /* Passwords don't match */
                    console.log('/userPassLogIn GET fail : password incorrect');

                    ret = {"success" : false};
                }

                res.json(ret);
            });
        } else {
            console.log('/userPassLogIn GET error : user does not exist');

            res.json({"success" : false});
        }
    });
});

app.post('/userPassSignUp', function(req, res) {
    console.log('/userPassSignUp POST');

    var user_email = req.body.email;
    var password = req.body.password;

    /* verify user doesn't exist in DB */
    var query = dbObj.collection("users").find({email:user_email}).toArray(function(err, result) {
        if(err) throw err;

        if(typeof result !== 'undefined' && result.length > 0) {
            /* pre-existing user, fail request */
            console.log('/userPassSignUp POST error : user exists');
            res.json({"success" : false});
        } else {
            console.log('/userPassSignUp POST : attempt to create user');

            /* create user in database */
            res.json({"success" : true});

            bcrypt.hash(password, 10, function(err, hash) {
                if(err) throw err;

                /* Store hash in database */
                var newUser = {email : user_email, password : hash};
                dbObj.collection("users").insertOne(newUser, function(err, res) {
                    if(err) throw err;

                    console.log("/userPassSignUp POST success : Created new user");
                });
            });
        }
    });
});

app.post('/notif_test', function(req, res) {
    console.log("/notif_test GET");
    var newReq = {
        name: req.body.name,
        description: req.body.description,
        lat: req.body.lat,
        long: req.body.long,
        userId: req.body.userId
    };

    console.log(newReq)
    /* prepare message */
    var message = new gcm.Message({
        data : {key1 : 'mgs1'},
        notification: {
            title: newReq.userId + " requested " + newReq.name,
            icon: "ic_launcher",
            body: newReq.description
        }
    });

    /* notify all users */
    var query = dbObj.collection("users").find().toArray(function(err, result) {
        if(err) throw err;

        /* append user reg tokens here */
        var regTokens = [];

        result.forEach(function(item, index) {
            regTokens.push(item['fcm_tok']);
        });

        sender.send(message, {registrationTokens : regTokens}, function(err, resp) {
            if(err)
                console.error(err);
            else
                console.log(resp);
        });
    });
});

/*********************************************************************
 * HELPER FUNCTIONS
 *********************************************************************/

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