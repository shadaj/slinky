var opt = require("./docs-opt.js");

if (typeof ssr !== 'undefined') {
    module.exports = function render(locals, callback) {
        var publicSSR = {};
        window.getPublic = function(page) {
            var pageLocation = "../../../../public" + page;
            var ret = fs.readFileSync(pageLocation);
            publicSSR[page] = String(ret);
            return ret;
        };

        var ssred = opt.entrypoint.ssr(locals.path);
        callback(
            null,
            '<!DOCTYPE html>' +
            '<html>' +
                '<head>' +
                    '<meta charset="utf-8">' +
                    '<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">' +
                    '<meta name="theme-color" content="#000000">' +
                    '<link rel="manifest" href="/manifest.json">' +
                    '<link rel="shortcut icon" href="/favicon.ico">' +
                    '<title>Slinky</title>' +
                    document.getElementsByTagName("head")[0].innerHTML +
                '</head>' +
                '<body>' +
                    '<div id="root">' +
                        ssred +
                    '</div>' +
                    '<script type="text/javascript">window.publicSSR = ' + JSON.stringify(publicSSR) + '</script>' +
                    '<script async src="/docs-opt-bundle.js"></script>' +
                '</body>' +
            '</html>');
    };
} else {
    opt.entrypoint.hydrate();
}