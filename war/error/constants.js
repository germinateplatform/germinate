var zero = [
	{ x: 0, y: 0},
    { x: 0, y: 20},
    { x: 0, y: 40},
    { x: 0, y: 60},
    { x: 0, y: 80},
    { x: 0, y: 100},
    { x: 0, y: 120},
    { x: 0, y: 140},
    { x: 0, y: 160},
    { x: 0, y: 180},
    { x: 0, y: 200},
    { x: 0, y: 220},

    { x: 20, y: 220},
    { x: 40, y: 220},
    { x: 60, y: 220},
    { x: 80, y: 220},

    { x: 100, y: 0},
    { x: 100, y: 20},
    { x: 100, y: 40},
    { x: 100, y: 60},
    { x: 100, y: 80},
    { x: 100, y: 100},
    { x: 100, y: 120},
    { x: 100, y: 140},
    { x: 100, y: 160},
    { x: 100, y: 180},
    { x: 100, y: 200},
    { x: 100, y: 220},

    { x: 20, y: 0},
    { x: 40, y: 0},
    { x: 60, y: 0},
    { x: 80, y: 0}
];

var one = [
	{ x: 100, y: 0},
    { x: 100, y: 20},
    { x: 100, y: 40},
    { x: 100, y: 60},
    { x: 100, y: 80},
    { x: 100, y: 100},
    { x: 100, y: 120},
    { x: 100, y: 140},
    { x: 100, y: 160},
    { x: 100, y: 180},
    { x: 100, y: 200},
    { x: 100, y: 220}
];

var three = [
	{ x: 20, y: 200},
    { x: 40, y: 200},
    { x: 60, y: 200},
    { x: 80, y: 200},

	{ x: 100, y: 0},
    { x: 100, y: 20},
    { x: 100, y: 40},
    { x: 100, y: 60},
    { x: 100, y: 80},
    { x: 100, y: 100},
    { x: 100, y: 120},
    { x: 100, y: 140},
    { x: 100, y: 160},
    { x: 100, y: 180},
    { x: 100, y: 200},
    { x: 100, y: 220},

    { x: 20, y: 100},
    { x: 40, y: 100},
    { x: 60, y: 100},
    { x: 80, y: 100},

    { x: 20, y: 0},
    { x: 40, y: 0},
    { x: 60, y: 0},
    { x: 80, y: 0}
];

var four = [
	{ x: 0, y: 0},
    { x: 0, y: 20},
    { x: 0, y: 40},
    { x: 0, y: 60},
    { x: 0, y: 80},
    { x: 0, y: 100},

    { x: 20, y: 100},
    { x: 40, y: 100},
    { x: 60, y: 100},
    { x: 80, y: 100},

	{ x: 100, y: 0},
    { x: 100, y: 20},
    { x: 100, y: 40},
    { x: 100, y: 60},
    { x: 100, y: 80},
    { x: 100, y: 100},
    { x: 100, y: 120},
    { x: 100, y: 140},
    { x: 100, y: 160},
    { x: 100, y: 180},
    { x: 100, y: 200},
    { x: 100, y: 220}
];

var fourZeroFour = "<h3>All right then, if it's resting I'll wake it up. Hello Polly! I've got a nice cuttlefish for you when you wake up, Polly parrot!</h3><p>Like this parrot, the link that you just clicked on is dead.</p><p>It's not pining, it's passed on. This page is no more. It has ceased to be. It's expired and gone to meet its maker. This is a late web page. It's a stiff. Bereft of life, it rests in peace. If you hadn't nailed it to the server, it would be pushing up the daisies. It's rung down the curtain and joined the choir invisible. This is an ex-page.</p><p>If you are feeling particularly helpful you could even let us know about this problem on our email address <a href='mailto:germinate@hutton.ac.uk' style='color: gray;'>germinate@hutton.ac.uk</a> - just tell us what you were doing when this happened.</p><p>A nod's as good as a wink to a blind bat, so follow one of the links below and we won't say any more about this indiscretion.</p><p>ps. he's only sleeping...</p>";
var fourZeroThree = "<h3>Follow! But follow only if ye be men of valour for access to this file is guarded by a creature...</h3><p>...so foul, so cruel that no man yet has tried to get it and lived.</p><p>Bones of 50 men lie strewn about its lair. So, brave web user if you do doubt your courage or your strength, come no further for death awaits you all with nasty, big, pointy teeth!</p>What an eccentric performance! Follow one of the links below and we wont say any more about this.</p>";
var fourZeroOne = "<h3>I didn't expect a kind of Spanish Inquisition...</h3><p>Nobody expects the Spanish Inquisition!</p><p>Our chief weapon is surprise...surprise and fear...fear and surprise.... Our two weapons are fear and surprise...and ruthless efficiency.... Our *three* weapons are fear, surprise, and ruthless efficiency...and an almost fanatical devotion to bioinformatics.... Our *four*...no... *Amongst* our weapons.... Amongst our weaponry...are such elements as fear, surprise....</p><p>I'll come in again.</p><p>Confess and follow one of the links below and we wont say any more about this indiscretion.</p>";

var bunny = "bunny.png";
var inquisition = "inquisition.png";
var parrot = "dead-parrot.png";

window.errorPageNumbers = [];
window.errorPageNumbers[0] = zero;
window.errorPageNumbers[1] = one;
window.errorPageNumbers[3] = three;
window.errorPageNumbers[4] = four;

window.errorPageText = {};
window.errorPageText["404"] = fourZeroFour;
window.errorPageText["403"] = fourZeroThree;
window.errorPageText["401"] = fourZeroOne;

window.errorPageImages = {};
window.errorPageImages["404"] = parrot;
window.errorPageImages["403"] = bunny;
window.errorPageImages["401"] = inquisition;

window.myFancyCloneFunction = function clone(obj) {
    // Handle the 3 simple types, and null or undefined
    if (null == obj || "object" != typeof obj) return obj;

    var copy;
    // Handle Date
    if (obj instanceof Date) {
        copy = new Date();
        copy.setTime(obj.getTime());
        return copy;
    }

    // Handle Array
    if (obj instanceof Array) {
        copy = [];
        for (var i = 0, len = obj.length; i < len; i++) {
            copy[i] = clone(obj[i]);
        }
        return copy;
    }

    // Handle Object
    if (obj instanceof Object) {
        copy = {};
        for (var attr in obj) {
            if (obj.hasOwnProperty(attr)) copy[attr] = clone(obj[attr]);
        }
        return copy;
    }

    throw new Error("Unable to copy obj! Its type isn't supported.");
};