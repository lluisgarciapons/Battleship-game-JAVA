var isOkToShoot = false;
var shots = 0;

function salvoListeners () {
    $("#op-table-rows .tcell").mouseenter(salvoMouseEnter);
    $("#op-table-rows .tcell").mouseleave(removeHighlightedSalvoCell);
    $("#op-table-rows .tcell").click(onClickSalvo);
}

function salvoMouseEnter() {
    isOkToShoot = checkForSalvoConflicts(this);
    if (shots < 5) {
        highlightSalvoCell(this);
    }
    if ($(this).hasClass("shot")) {
        $(this).addClass("highlight_replace");
    }
}

function checkForSalvoConflicts(element) {
    if ($(element).hasClass("hit") || $(element).hasClass("miss")) {
        return false;
    } else {
        return true;
    }
}

function highlightSalvoCell(element) {
    if (!$(element).hasClass("miss") && !$(element).hasClass("hit") && !$(element).hasClass("shot")) {
        $(element).addClass("target");
    }
}

function removeHighlightedSalvoCell() {
    $(this).removeClass("target");
    $(this).removeClass("highlight_replace");
}

function onClickSalvo() {
    if ($(this).hasClass("shot")){
        replaceShot(this);
    } else if(shots < 5){
        shootSalvo(this);
    } else {
        $("#error-alert").text("You can't shoot more than 5 times. Click on a bomb to change its position.");
        $("#error-modal").modal("show");
    }
}

function shootSalvo(element) {
    if (isOkToShoot == true) {
        $(element).addClass("shot");
        $(element).removeClass(("target"));
        shots++;
        missilesUsed();
    } else  {
        $("#error-alert").text("Invalid position.");
        $("#error-modal").modal("show");
    }
}

function replaceShot(element) {
    $(element).removeClass("shot");
    shots--;
    missilesUsed();
}

function missilesUsed() {
    $(".missile").removeClass("missile2");
    for (var i = 1; i <= shots; i++) {
        $("#shot" + i).addClass("missile2");
    }
}

function postShots() {
    if (shots < 5) {
        $("#warning-alert").text("You are only firing " + shots + " missile(s), do you want to continue?");
        $("#warning-modal").modal("show");
    } else {
        sendShots();
    }
}

function sendShots() {
    var ids = $(".shot").map(function() {
        var id = $(this).attr("id");
        return id.slice(0, -2);
    });
    var locationShot = Array.from(ids);
    console.log(location);
    postSalvoes({locations: locationShot});
    location.reload();
}