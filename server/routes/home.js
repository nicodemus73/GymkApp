const express = require('express');
const router = express.Router();

router.get('/', (req, res) => {
    res.status(200).send("This is home. Test autodeploy 1");
});

module.exports = router;