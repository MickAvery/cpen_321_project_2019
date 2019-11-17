const express = require('express');
const router  = express.Router();
var mainMod   = require("../index.js");

const azureServerURL = "https://ingredishare-backend.azurewebsites.net";
const localServerURL = "http://localhost:1337";

router.get('/', (req, res) => {
    res.send("Hello world!");
});

async function getProfileInfo(obj) {
    var dbObj = mainMod.getDb();

    const result = await dbObj.collection("users").find({email: obj.email}, {projection: {displayName: 1, bio: 1, preferences: 1, radiusPreference: 1}}).toArray();
    return result;
}

router.get('/getProfileInfo', (req, res) => {
    var email = req.query.email;

    try {
        getProfileInfo(email).then((result) => {
            res.json(result);
        });
    } catch (err) {
        throw err;
    }
});

async function getProfileInfo(email) {
    var dbObj = mainMod.getDb();

    const result = await dbObj.collection("users").findOne({email: email}, {projection: {displayName: 1, bio: 1, preferences: 1}});
    return result;
}

router.post("/updateProfileInfo", (req, res) => {
    var myquery = { email: req.body.email };
    var newvalues = { $set: {displayName: req.body.displayName, bio: req.body.bio,
            preferences: req.body.preferences, radiusPreference: req.body.radius_preference} };

    dbObj.collection("users").updateOne(myquery, newvalues, function(err, res) {
        if (err) {
            throw err
        };
    });

    res.json({"updateProfileInfo": true});
});

module.exports = router;
