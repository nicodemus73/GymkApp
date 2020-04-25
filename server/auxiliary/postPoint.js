const Point = require('../bbdd/PointSchema');

module.exports = async function postPoint(req, body) {

    var code, res;
    try {
        const point = new Point({
            name: body.name,
            owner: req.usernameId._id,
            type: body.type,
            description: body.description,
            public: body.public,
            location: body.location
        });

        const savedPoint = await point.save();

        code = 200;
        res = savedPoint;

        return { code: code, result: res };

    } catch (err) {

        if (err.message.substring(0, 6) == 'E11000') {
            code = 409;
            res = {"error":'Point already exists.'};
        }
        else {
            code = 400;
            res = { "error": err.message };
        }
        return { code: code, result: res };
    }
}