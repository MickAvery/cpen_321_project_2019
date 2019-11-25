const express = require("express");
const router  = new express.Router();
var mainMod   = require("../index.js");

const azureServerURL = "https://ingredishare-backend.azurewebsites.net";
const localServerURL = "http://localhost:1337";

async function getAllRequests() {
    var dbObj = mainMod.getDb();
    if (dbObj === null) {
        throw "Could not connect to DB";
    }
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
    createRequest(req, res);
});

async function createRequest(req, res) {
    try {
        var dbObj = mainMod.getDb();

        if (dbObj === null) {
            throw "Could not connect to DB";
        }

        var newReq = {
            userId: req.body.userId,
            name: req.body.name,
            description: req.body.description,
            lat: req.body.lat,
            long: req.body.long,
            type: req.body.type
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
}

module.exports = {
    router: router,
    getAllRequests: getAllRequests,
    getAllRequestsFromLatLong: getAllRequestsFromLatLong,
    requestIsValid: requestIsValid,
    createRequest: createRequest,
};
