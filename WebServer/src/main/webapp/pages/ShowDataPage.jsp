<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="../css/GeneralStyle.css">
    <link rel="stylesheet" href="../css/ShowDataPage.css">
    <title id="tab_title">Data</title>
</head>
<body>
<header>
    <ul>
        <li><a href="/">Home</a></li>
    </ul>
</header>
<div id="data_page">
    <h1 class="main_title" id="main_title">Data Table</h1>
    <div id="data_table">
        <table>
            <thead>
            <tr>
                <!-- Table headers will be dynamically generated -->
            </tr>
            </thead>
            <tbody>
            <!-- Table rows will be dynamically generated -->
            </tbody>
        </table>
    </div>
    <div class="pagination">
        <button onclick="changePage(-1)">Previous</button>
        <span id="current-page" class="current-page">1</span>
        <button onclick="changePage(1)">Next</button>
    </div>
</div>
</body>
<script>
    <%
        String jsonString = (String) request.getAttribute("jsonString");
    %>
    const jsonData = '<%=jsonString%>';
    const jsonObject = JSON.parse(jsonData);
    const rowsData = jsonObject.data;
    const pathToDetailsRow = jsonObject.path;
    const page_title = jsonObject.title;

    document.getElementById("tab_title").innerHTML = page_title;
    document.getElementById("main_title").innerHTML = page_title + " Table";

    const rowsPerPage = 6;
    let currentPage = 1;


    function createTable(page) {
        const table = document.getElementById('data_table');
        const thead = table.getElementsByTagName('thead')[0];
        const tbody = table.getElementsByTagName('tbody')[0];

        // Clear any existing table data
        thead.innerHTML = '';
        tbody.innerHTML = '';

        // Create table headers
        const headers = Object.keys(rowsData['Row1']);
        const headerRow = document.createElement('tr');
        const th = document.createElement('th');
        th.textContent = 'Row';
        headerRow.appendChild(th);
        headers.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header;
            headerRow.appendChild(th);
        });
        thead.appendChild(headerRow);

        // Calculate pagination
        const rowKeys = Object.keys(rowsData); // return array of the keys in data property
        const startIndex = (page - 1) * rowsPerPage;
        const endIndex = Math.min(startIndex + rowsPerPage, rowKeys.length);

        // Create table rows for current page
        for (let i = startIndex; i < endIndex; i++) {
            const rowKey = rowKeys[i];
            const tr = document.createElement('tr');
            const rowTd = document.createElement('td');
            rowTd.textContent = rowKey;
            tr.appendChild(rowTd);
            headers.forEach(header => {
                const td = document.createElement('td');
                td.textContent = rowsData[rowKey][header];
                tr.appendChild(td);
            });
            tbody.appendChild(tr);
        }

        const rows = tbody.getElementsByTagName('tr');
        Array.from(rows).forEach(row => {
            row.addEventListener('click', onRowClick)
        })

        // Update current page in page
        document.getElementById('current-page').textContent = page;
    }

    function changePage(direction) {
        const rowKeys = Object.keys(rowsData);
        const totalPages = Math.ceil(rowKeys.length / rowsPerPage);

        currentPage += direction;

        // Ensure page stays within bounds
        currentPage = Math.max(1, Math.min(currentPage, totalPages));

        createTable(currentPage);
    }

    function onRowClick(clickEvent){
        if(null != pathToDetailsRow){
            const clickedRow = clickEvent.currentTarget;
            const id = clickedRow.childNodes[1].innerHTML;
            window.location.href = pathToDetailsRow + id;
        }
    }

    // Start from page 1
    createTable(1);
</script>
</html>