const express = require("express");
const router  = new express.Router();
var mainMod   = require("../index.js");

const azureServerURL = "https://ingredishare-backend.azurewebsites.net";
const localServerURL = "http://localhost:1337";

async function getAllRequests() {
    var dbObj = mainMod.getDb();
    const result = await dbObj.collection("requests").find({}).toArray();
    return result;
}

router.get("/getAllRequests", (req, res) => {
    getAllRequests().then((result) => {
        res.json(result);
    });
});

async function getAllRequestsFromLatLong(temp) {
    var dbObj = mainMod.getDb();
    if (dbObj === null) {
        throw "Could not connect to DB";
    }

    if (temp.email === undefined) {
        throw "Req json is missing email field.";
    }

    if (temp.lat < -90 || temp.lat > 90) {
        throw "Invalid latitude";
    }

    if (temp.long < -180 || temp.long > 180) {
        throw "Invalid longitude";
    }

    const user = await dbObj.collection("users").findOne(
        {email : temp.email},
        {radiusPreference : 1}
    );

    var radiusPref = user.radiusPreference;
    var latRange = Number(radiusPref) * (Number(1) / Number(110.574));
    var longRange = Number(radiusPref) * (Number(1) / (Number(111.32) * Math.cos(temp.lat)));

    const result = await dbObj.collection("requests").find({
        lat: { $gt: (Number(temp.lat)-Number(latRange)), $lt: (Number(temp.lat)+Number(latRange))},
        long: { $gt: (Number(temp.long)-Number(longRange)), $lt: (Number(temp.long)+Number(longRange))}
    }).toArray();

    return result;
}

router.get("/getAllRequestsFromLatLong", (req, res) => {

    var temp = {
        lat: req.query.lat,
        long: req.query.long,
        email: req.query.email
    };

    try {
        getAllRequestsFromLatLong(temp).then((result) => {
            res.json(result);
        });
    } catch (err) {
        res.status(500).end();
    }
});

function requestIsValid(newRequest) {
    var ret = true;

    Object.values(newRequest).forEach(function(value, index) {
        if(!value) {
            ret = false;
            /* TODO: calling break here throws an "Unsyntactic break" */
            /*       I FUCKING HATE JAVASCRIPT */
        }
    });

    return ret;
}

router.post("/createRequest", (req, res) => {
    try {
        var dbObj = mainMod.getDb();

        var newReq = {
            userId: req.body.userId,
            name: req.body.name,
            description: req.body.description,
            lat: req.body.lat,
            long: req.body.long,
            type: req.body.type,
            date: req.body.date
        };

        /* gotta check if any of the fields are falsey */
        if(requestIsValid(newReq)) {

            dbObj.collection("requests").insertOne(newReq, function(err,dbRes) {
                if(err) {
                    res.json({"createRequestResponse": false});
                } else {
                    res.json({"createRequestResponse": true});
                }
            });

            return;
        }

        res.json({"createRequestResponse": false});
    } catch (err) {
        res.status(500).end();
    }
});

router.get("/isExistingUser", (req, res) => {
    var dbObj = mainMod.getDb();

    var userEmail = req.body.email;

    var query = dbObj.collection("users").find({email:userEmail}).toArray(function(err, result) {
        if(err) {

            res.send(500).end();

        } else if(typeof result !== "undefined" && result.length > 0) {

            res.json({"pre_existing_user" : true});

        } else {

            res.json({"pre_existing_user" : false});

            var newUser = {email : userEmail};
            dbObj.collection("users").insertOne(newUser, function(err, res) {
                if(err) {
                    res.status(500).end();
                }
            });
        }
    });
});

module.exports = {
    router: router,
    getAllRequests: getAllRequests,
    getAllRequestsFromLatLong: getAllRequestsFromLatLong,
    requestIsValid: requestIsValid
};
