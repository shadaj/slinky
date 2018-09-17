var opt = require("./slinky-docs-opt.js");

if (typeof ssr !== 'undefined') {
    module.exports = function render(locals, callback) {
      callback(null, opt.entrypoint.ssr(locals.path));
    };
} else {
    opt.entrypoint.hydrate();
}