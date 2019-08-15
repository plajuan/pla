'use strict';
const puppeteer = require('puppeteer');
const sql = require('mssql');
/*
Get data from OTRS
Fill form on Redmine
Submit form for approval on DataBase
*/
 
(async () => {

  const browser = await puppeteer.launch({
    headless: false, 
    defaultViewport: null,
    executablePath: "path to chrome.exe on local machine",
    args: [`--start-maximized`]
  });

  const page = await browser.newPage();

  await page.goto('OTRS URL', {waitUntil: 'load'});
  await page.type('#User', "user");
  await page.type('#Password', 'password');

  await page.click('#LoginButton');

  await page.goto(`http://OTRS_URL/otrs/index.pl?Action=AgentTicketZoom;TicketNumber=${process.argv[2]}`);
  
  const result = await page.evaluate( () => {
    let resp = {}
    let line = document.querySelector('h1').innerText;
    resp["ticket"] = line.split(' ')[0].split('#')[1];
    resp["titulo"] = line.split(' ').slice(2,line.length).join(' ');
    let temp = document.querySelectorAll('label+p.Value');
    
    if (temp[4].title != 'Incidente'){      
      throw "O ticket não é um incidente";
    }

    resp["registradoPor"] = temp[32].title.split(' <')[0];
    resp["contato"] = temp[27].title;
    resp["data"] = temp[6].title.split(' ')[0];
    resp["hora"] = temp[6].title.split(' ')[1];
    resp["impacto"] = temp[12].title;
    resp["status"] = temp[7].title;
    resp["area"] = temp[19].innerText.trim();
    resp["servico"] = temp[10].title;
    return resp;    
  })
    
  await page.goto('http://redmineURL/login');

  await page.type('#username', 'user');
  await page.type('#password', 'password');

  await page.click('#login-submit');

  await page.goto('http://redmineURL/projects/your_project/issues/new');
  await page.type('#issue_subject', result.titulo);
  await page.type('#issue_custom_field_values_2', result.ticket);
  await page.type('#issue_custom_field_values_6', result.registradoPor);
  await page.type('#issue_custom_field_values_7', result.contato);
  await page.type('#issue_custom_field_values_13', result.status);
  await page.type('#issue_custom_field_values_14', result.area);
  await page.type('#issue_custom_field_values_15', result.servico);
  await page.type('#issue_custom_field_values_16', result.impacto);
  await page.select('#issue_custom_field_values_8', result.impacto.split(' - ')[1] );
  await page.type('#issue_custom_field_values_11', result.data);
  await page.type('#issue_custom_field_values_5', result.hora);

  try {
    //HOMOLOG 90
    await sql.connect('mssql://user:password@server/DB');
    
    if (true){
      let str = `INSERT INTO 
      ) VALUES (
       
       )
     `;
     
     let result = await sql.query(str);
     console.log(result);
     str = `select id from where epnum like '${process.argv[2]}' `;
     result = await sql.query(str);
          
     str = `INSERT INTO 
      )VALUES(
      )
    `;    
    await sql.query(str);
    }    
  } catch (err) {
    console.log(err);
  }
  
})();

