const express = require('express');
const distance = require('turf-distance');
const router = express.Router();

router.get('/', (req, res) => {

  console.log(Number(req.query.lon));
  console.log(Number(req.query.lat));
  console.log(Number(req.query.radius));

  const location = { "type": "Point", "coordinates": [Number(req.query.lon), Number(req.query.lat)] }
  console.log(location);

  console.log(distance(location, location));

  res.status(200).send('This is home GET');
});

router.post('/', (req, res) => {
  res.status(200).send('This is home POST');
});

module.exports = router;