<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Usage Report</title>
<link rel="stylesheet" href="style.css" />
</head>
<body>
<h1>Usage Log Report</h1><hr />
<p>
${info}
</p>
<hr />
<h3>Active Users - No logs</h3>
<table id='tb0' cellpadding="0" cellspacing="0" border="0" class="sortable">
<thead>
<tr>
<th><h3>ID</h3></th><th><h3>Name</h3></th>
</tr>
</thead>
<tbody>
${tb0}
</tbody>
</table>
<h3>Active Users - Activities</h3>
<table id='tb1' cellpadding="0" cellspacing="0" border="0" class="sortable">
<thead>
<tr>
<th><h3>ID</h3></th><th><h3>Name</h3></th><th><h3># of accesses</h3></th><th><h3>Last Logon</h3></th>
<th><h3>Requisitions</h3></th><th><h3>Req approvals</h3></th><th><h3>Reimbursement</h3></th>
<th><h3>reimb approvals</h3></th><th><h3>Contracts</h3></th><th><h3>Valuations</h3></th>
<th><h3>Approvals</h3></th>
</tr>
</thead>
<tbody>
${tb1}
</tbody>
</table>
<h3>SOFTWAY Usage</h3>
<table id='tb3' cellpadding="0" cellspacing="0" border="0" class="sortable">
<thead>
<tr>
<th><h3>ID</h3></th><th><h3>Name</h3></th><th><h3>Company</h3></th><th><h3>Last Logon</h3></th><th><h3>Usage Time</h3></th><th><h3>Accesses (2015)</h3></th>
</tr>
</thead>
<tbody>
${tb3}
</tbody>
</table>
<h3>Inactive Users</h3>
<table id='tb2' cellpadding="0" cellspacing="0" border="0" class="sortable">
<thead>
<tr>
<th><h3>ID</h3></th><th><h3>Name</h3></th><th><h3># of accesses</h3></th><th><h3>Last Logon</h3></th>
</tr>
</thead>
<tbody>
${tb2}
</tbody>
</table>
<script type="text/javascript" src="script.js"></script>
<script type="text/javascript">
var sorter0 = new TINY.table.sorter("sorter0");
sorter0.head = "head";
sorter0.asc = "asc";
sorter0.desc = "desc";
sorter0.even = "evenrow";
sorter0.odd = "oddrow";
sorter0.evensel = "evenselected";
sorter0.oddsel = "oddselected";
sorter0.paginate = false;
sorter0.currentid = "currentpage";
sorter0.limitid = "pagelimit";
sorter0.init("tb0",1);
	
var sorter1 = new TINY.table.sorter("sorter1");
sorter1.head = "head";
sorter1.asc = "asc";
sorter1.desc = "desc";
sorter1.even = "evenrow";
sorter1.odd = "oddrow";
sorter1.evensel = "evenselected";
sorter1.oddsel = "oddselected";
sorter1.paginate = false;
sorter1.currentid = "currentpage";
sorter1.limitid = "pagelimit";
sorter1.init("tb1",1);

var sorter2 = new TINY.table.sorter("sorter2");
sorter2.head = "head";
sorter2.asc = "asc";
sorter2.desc = "desc";
sorter2.even = "evenrow";
sorter2.odd = "oddrow";
sorter2.evensel = "evenselected";
sorter2.oddsel = "oddselected";
sorter2.paginate = false;
sorter2.currentid = "currentpage";
sorter2.limitid = "pagelimit";
sorter2.init("tb2",1);
  </script>
</body>
</html>