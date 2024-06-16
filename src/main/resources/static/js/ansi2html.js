export function processString(inputString) {
    var str = makeString(inputString);
    while (true) {
        var openSpanCount = (str.match(/<span/g) || []).length;
        var closeSpanCount = (str.match(/<\/span/g) || []).length;
        if (openSpanCount >= closeSpanCount) {
            break;
        }
        // we may have some extra closing escape sequence that doesn't really close anything
        // especially on end of line but not only
        var underReplace = '<\/span>';
        var idx = str.lastIndexOf(underReplace);
        if (idx > -1) {
            str = str.substring(0, idx) + str.substring(idx + underReplace.length);
        }
    }
    return str;
}

/**
 * Convert the text from `inputString` and return it
 * @param {Object} inputString
 * @return {String}
 */
export function ansi2html_string(inputString) {
    return processString(inputString).trim();
}

function makeString(str) {
    //
    // `\033[1m` enables bold font, `\033[21m` disables it, '\033[24m` - disables italic (introduced by 21) - not here
    //
    str = str.replace(/\033\[1m/g, '<b>').replace(/\033\[21m/g, '</b>').replace(/\033\[24m/g, '');

    //
    // `\033[3m` enables italics font, `\033[23m` disables it
    //
    str = str.replace(/\033\[3m/g, '<i>').replace(/\033\[23m/g, '</i>');

    //
    // `\033[9m` enables strikethrough, `\033[29m` disables it
    //
    str = str.replace(/\033\[9m/g, '<s>').replace(/\033\[29m/g, '</s>');

    //
    // `\033[Xm` == `\033[0;Xm` sets foreground color to `X`.
    //
    str = str.replace(
        /(\033\[(\d+)(;\d+)?m)/gm,
        function (match, fullMatch, m1, m2) {
            var fgColor = m1;
            var bgColor = m2;

            var newStr = '<span class="';
            if (fgColor && foregroundColors[fgColor]) {
                newStr += 'ansi_fg_' + foregroundColors[fgColor];
            }
            if (bgColor) {
                bgColor = bgColor.substr(1); // remove leading ;
                if (backgroundColors[bgColor]) {
                    newStr += ' ansi_bg_' + backgroundColors[bgColor];
                }
            }
            newStr += '">';
            return newStr === "<span class=\"\">" ? "" : newStr;
        }
    );

    str = str.replace(/\033\[m/g, '');
    str = str.replace(/\033\[0m/g, '');
    return str.replace(/\033\[39m/g, '</span>');
}

var foregroundColors = {
    '30': 'black',
    '31': 'red',
    '32': 'green',
    '33': 'yellow',
    '34': 'blue',
    '35': 'purple',
    '36': 'cyan',
    '38': 'white'
};

var backgroundColors = {
    '40': 'black',
    '196': 'red',
    '42': 'green',
    '43': 'yellow',
    '44': 'blue',
    '45': 'purple',
    '46': 'cyan',
    '47': 'white',
    '68': '68m'
};