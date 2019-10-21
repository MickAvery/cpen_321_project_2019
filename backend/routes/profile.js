const express = require('express')
const router = express.Router()

router.get('/', (req, res) => {
    res.send("Hello world!");
});

router.get('/getProfileInfo', (req, res) => {
    try {
        getProfileInfo(req.body.email).then((result) => {
            res.json(result);
        });
    } catch (err) {
    }
});

async function getProfileInfo(userId) {
    const result = await dbIngrediShare.collection("users").find({email: userId}).toArray();
    return result;
}

// router.post('/updateProfileInfo', (req, res) => {
//     try {
//         var newProfileInfo = {
//             displayName: req.body.full_name, 
//             bio: req.body.bio,
//             preferences: req.body.preferences 
//         };
//         if(newProfileInfo.displayName === undefined || 
//             newProfileInfo.bio === undefined ||
//             newProfileInfo.preferences === undefined
//         ){
//                 res.json({"updateProfileInfo": false}); return;
//             }
//         dbIngrediShare.collection("users").insertOne(newProfileInfo, function(err,res) {
//             if(err){
//                 res.json({"updateProfileInfo": false});
//                 throw err;
//             } 
//         })
//         res.json({"updateProfileInfo": true});
//     } catch (err){}
// });

module.exports = router