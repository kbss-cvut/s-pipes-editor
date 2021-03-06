(function (f) {
    if (typeof exports === "object" && typeof module !== "undefined") {
        module.exports = f()
    } else if (typeof define === "function" && define.amd) {
        define([], f)
    } else {
        let g;
        if (typeof window !== "undefined") {
            g = window
        } else if (typeof global !== "undefined") {
            g = global
        } else if (typeof self !== "undefined") {
            g = self
        } else {
            g = this
        }
        (g.ReactIntlLocaleData || (g.ReactIntlLocaleData = {})).cs = f()
    }
})(function () {
    let define, module, exports;
    return (function e(t, n, r) {
        function s(o, u) {
            if (!n[o]) {
                if (!t[o]) {
                    const a = typeof require === "function" && require;
                    if (!u && a) return a(o, !0);
                    if (i) return i(o, !0);
                    const f = new Error("Cannot find module '" + o + "'");
                    throw f.code = "MODULE_NOT_FOUND", f
                }
                const l = n[o] = {exports: {}};
                t[o][0].call(l.exports, function (e) {
                    const n = t[o][1][e];
                    return s(n ? n : e)
                }, l, l.exports, e, t, n, r)
            }
            return n[o].exports
        }

        const i = typeof require === "function" && require;
        for (let o = 0; o < r.length; o++) s(r[o]);
        return s
    })({
        1: [function (require, module, exports) {
            "use strict";
            Object.defineProperty(exports, "__esModule", {value: !0}), exports["default"] = [{
                locale: "cs",
                pluralRuleFunction: function (e, a) {
                    const n = String(e).split("."), t = n[0], o = !n[1];
                    return a ? "other" : 1 === e && o ? "one" : t >= 2 && 4 >= t && o ? "few" : o ? "other" : "many"
                },
                fields: {
                    year: {
                        displayName: "Rok",
                        relative: {0: "tento rok", 1: "p??????t?? rok", "-1": "minul?? rok"},
                        relativeTime: {
                            future: {
                                one: "za {0} rok",
                                few: "za {0} roky",
                                many: "za {0} roku",
                                other: "za {0} let"
                            },
                            past: {
                                one: "p??ed {0} rokem",
                                few: "p??ed {0} lety",
                                many: "p??ed {0} rokem",
                                other: "p??ed {0} lety"
                            }
                        }
                    },
                    month: {
                        displayName: "M??s??c",
                        relative: {0: "tento m??s??c", 1: "p??????t?? m??s??c", "-1": "minul?? m??s??c"},
                        relativeTime: {
                            future: {
                                one: "za {0} m??s??c",
                                few: "za {0} m??s??ce",
                                many: "za {0} m??s??ce",
                                other: "za {0} m??s??c??"
                            },
                            past: {
                                one: "p??ed {0} m??s??cem",
                                few: "p??ed {0} m??s??ci",
                                many: "p??ed {0} m??s??cem",
                                other: "p??ed {0} m??s??ci"
                            }
                        }
                    },
                    day: {
                        displayName: "Den",
                        relative: {0: "dnes", 1: "z??tra", 2: "poz??t????", "-1": "v??era", "-2": "p??edev????rem"},
                        relativeTime: {
                            future: {
                                one: "za {0} den",
                                few: "za {0} dny",
                                many: "za {0} dne",
                                other: "za {0} dn??"
                            },
                            past: {
                                one: "p??ed {0} dnem",
                                few: "p??ed {0} dny",
                                many: "p??ed {0} dnem",
                                other: "p??ed {0} dny"
                            }
                        }
                    },
                    hour: {
                        displayName: "Hodina",
                        relativeTime: {
                            future: {
                                one: "za {0} hodinu",
                                few: "za {0} hodiny",
                                many: "za {0} hodiny",
                                other: "za {0} hodin"
                            },
                            past: {
                                one: "p??ed {0} hodinou",
                                few: "p??ed {0} hodinami",
                                many: "p??ed {0} hodinou",
                                other: "p??ed {0} hodinami"
                            }
                        }
                    },
                    minute: {
                        displayName: "Minuta",
                        relativeTime: {
                            future: {
                                one: "za {0} minutu",
                                few: "za {0} minuty",
                                many: "za {0} minuty",
                                other: "za {0} minut"
                            },
                            past: {
                                one: "p??ed {0} minutou",
                                few: "p??ed {0} minutami",
                                many: "p??ed {0} minutou",
                                other: "p??ed {0} minutami"
                            }
                        }
                    },
                    second: {
                        displayName: "Sekunda",
                        relative: {0: "nyn??"},
                        relativeTime: {
                            future: {
                                one: "za {0} sekundu",
                                few: "za {0} sekundy",
                                many: "za {0} sekundy",
                                other: "za {0} sekund"
                            },
                            past: {
                                one: "p??ed {0} sekundou",
                                few: "p??ed {0} sekundami",
                                many: "p??ed {0} sekundou",
                                other: "p??ed {0} sekundami"
                            }
                        }
                    }
                }
            }, {locale: "cs-CZ", parentLocale: "cs"}], module.exports = exports["default"];
        }, {}]
    }, {}, [1])(1)
});