// printMyTable();
//
// function printMyTable() {
//
//     for (var i = 0; i < 11; i++) {
//         var tr = document.createElement("tr");
//         tr.setAttribute("class", "trow");
//
//         if (i == 0) {
//             for (k = 0; k < 11; k++) {
//                 var th = document.createElement("th");
//                 th.setAttribute("class", "header_cell");
//                 if (k > 0) {
//                     th.innerHTML = k;
//                 }
//                 tr.append(th);
//                 var selectorHeader = "#table-headers";
//                 $(selectorHeader).append(tr);
//             }
//         } else {
//             for (var j = 0; j < 11; j++) {
//                 var td = document.createElement("td");
//                 td.setAttribute("class", "tcell");
//
//                 td.setAttribute("id", String.fromCharCode(64 + i) + j);
//
//                 tr.append(td);
//                 if (j == 0) {
//                     td.innerHTML = String.fromCharCode(64 + i);
//                 }
//                 var selectorBody = "#table-rows";
//                 $(selectorBody).append(tr);
//             }
//         }
//     }
// }

function shipListeners() {
    $("[data-ship]").click(shipClick);
    $("#pl-table-rows .tcell").mouseenter(onMouseEnter);
    $("#pl-table-rows .tcell").mouseleave(removeHighlightCells);
    $("#pl-table-rows .tcell").click(placeShip);
    $("#switch-normal").click(changeOrientation);
}

var selected = false;
//var orientation;
var type;
var shipId;
var x;
var y;
var isItOkToPlaceShip;
var allShips = 5;

function shipClick() {
    console.log("shipClick called");
    $(".form-group").show();
    $("#retrieveDataButton").prop("disabled", true);
    if ($(this).data("orientation") == "horizontal") {
        $("#switch-normal").prop('checked', false);
    } else {
        $("#switch-normal").prop('checked', true);
    }
    $(this).unbind("click", removeShip);
    selected = true;
    //    orientation = $(this).data("orientation");
    shipId = $(this).attr("id");
    x = $(this).data("w");
    y = $(this).data("h");
    type = $(this).data("type");
    $("[data-ship]").removeClass("selected");
    $(this).addClass("selected");
    console.log("x=" + x, "y=" + y, shipId);
}

function onMouseEnter() {
    console.log("onMouseEnter called");
    var isOk = checkForConflicts(this);
    isItOkToPlaceShip = isOk;
    highlightCells(this, isOk);
}

function checkForConflicts(element) {
    var isOk;
    if ($(element).hasClass("placed_ship")) {
        isOk = false;
    } else if (x > y) {
        isOk = true;
        var colCount = 1;
        $(element).nextAll(':lt(' + (x - 1) + ')').each(function () {
            colCount++;
            if ($(this).hasClass("placed_ship")) {
                isOk = false;
            }
        })
        if (colCount < x) {
            isOk = false;
        }
    } else {
        var index = $(element).index();
        isOk = true;
        var rowCount = 1;
        $(element).parent().nextAll(':lt(' + (y - 1) + ')').each(function () {
            rowCount++;
            if ($(this).children().eq(index).hasClass("placed_ship")) {
                isOk = false;
            }
        });
        if (rowCount < y) {
            isOk = false;
        }
    }
    return isOk;
}

function highlightCells(element, isOk) {
    var color;
    if (isOk == true) {
        color = "highlighted";
    } else {
        color = "conflict";
    }
    if (selected) {
        $(element).addClass(color);
        if (x > y) {
            $(element).nextAll(':lt(' + (x - 1) + ')').addClass(color);
        } else {
            var index = $(element).index();
            $(element).parent().nextAll(':lt(' + (y - 1) + ')').each(function () {
                $(this).children().eq(index).addClass(color);
            });
        }
    }
}

function removeHighlightCells() {
    $(this).removeClass("highlighted conflict");
    if (x > y) {
        $(this).nextAll(':lt(' + (x - 1) + ')').removeClass("highlighted conflict");
    } else {
        var index = $(this).index();
        $(this).parent().nextAll(':lt(' + (y - 1) + ')').each(function () {
            $(this).children().eq(index).removeClass("highlighted conflict");
        })
    }
}

function placeShip() {
    if (selected) {
        if (isItOkToPlaceShip == true) {
            $(this).addClass("placed_ship")
                .attr("data-id", shipId)
                .attr("data-type", type);
            if (x > y) {
                $(this).nextAll(':lt(' + (x - 1) + ')').each(function () {
                    $(this).addClass("placed_ship")
                        .attr("data-id", shipId)
                        .attr("data-type", type);
                })
            } else {
                var index = $(this).index();
                $(this).parent().nextAll(':lt(' + (y - 1) + ')').each(function () {
                    $(this).children().eq(index).addClass("placed_ship")
                        .attr("data-id", shipId)
                        .attr("data-type", type);
                });
            }
            $(".form-group").hide();
            $(".used").unbind("mouseenter", hoverShip);
            $(".used").unbind("mouseleave", unHoverShip);
            selected = false;
            disableShip();
            canButtonBeEnabled();
        } else {
            console.log("error");
            $("#error-alert").text("You can NOT place your ship here!");
            $("#error-modal").modal("show");
        }
    } else {
        console.log("select a ship first");
        $("#error-alert").text("Please, select a ship first.");
        $("#error-modal").modal("show");
    }
}

function disableShip() {
    $("[data-ship]").removeClass("selected");
    $("#" + shipId).addClass("used");
    $(".used").mouseenter(hoverShip);
    $(".used").mouseleave(unHoverShip);
    //    $("#" + shipId).unbind("click", shipClick);
    $("#" + shipId).click(removeShip);
}

function removeShip() {
    $(this).removeClass("used hovered");
    shipId = $(this).attr("id");
    $("[data-id=" + shipId + "]").removeClass("placed_ship hovered_ship").removeAttr("data-id");
    //    $(this).click(shipClick);
}

function hoverShip() {
    $(this).addClass("hovered");
    var id = $(this).attr("id");
    $("[data-id='" + id + "']").addClass("hovered_ship");
}

function unHoverShip() {
    $(this).removeClass("hovered");
    var id = $(this).attr("id");
    $("[data-id='" + id + "']").removeClass("hovered_ship");
}

function changeOrientation() {
    var temporaryX = x;
    x = y;
    y = temporaryX;
    console.log("orientation changed", "x=" + x, "y=" + y, shipId);
}

function canButtonBeEnabled() {
    var result;
    for (var i = 1; i <= allShips; i++) {
        if ($("#ship" + i).hasClass("used")) {
            result = true;
        } else {
            result = false;
            break;
        }
    }
    if (result == true) {
        $("#retrieveDataButton").prop("disabled", false);
    }
}

function retrieveData() {
    var ships = [];
    console.log("ok it works, let's move on.");
    for (i = 1; i <= allShips; i++) {
        var ids = $("[data-id='ship" + i + "']").map(function () {
            return $(this).attr("id");
        });
        ships.push({
            "type": $("[data-id='ship" + i + "']").data("type"),
            "locations": Array.from(ids)
        });
        console.log(ids);
    }
    console.log(ships);
    postShips(ships);
    location.reload();
    $("#game-display").show();
    $("#place-ships").hide();
}
