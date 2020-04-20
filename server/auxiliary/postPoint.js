const Point = require('../bbdd/PointSchema');

module.exports = async function postPoint(req, body, res) {
    try {
        const point = new Point({
            name: body.name,
            owner: req.usernameId._id,
            type: body.type,
            description: body.description,
            public: body.public,
            location: body.location
        });

        await point.save(function (err, savedPoint) {
            if (err) {
                if (err.message.substring(0, 6) == 'E11000')
                    res.status(409).json({ "error": "Point already exists." });
                else res.status(400).json({ "error": err.message });
            }
            else res.status(200).json(savedPoint);
        });
    } catch (err) {
        res.status(500).json({ "error": err.message });
    }
}