var body = document.getElementsByTagName('body')[0];
var html = "<h1>Data available?</h1>";
if (knimeDataTable) {
    html += '<div class="success">Data available. Node correctly configured. Table contains ' + knimeDataTable.getNumRows() + ' rows.</div>';
} else {
    html += '<div class="failure">No data available.</div>';
}
body.innerHTML = html;
