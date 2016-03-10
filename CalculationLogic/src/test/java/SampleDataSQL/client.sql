INSERT INTO APP.CLIENT (ID,CITY,CREATIONDATE,NAME,STREET,TELEPHONENUMBER,ZIPCODE)
VALUES 
(1,'Linz',TIMESTAMP('2015-02-23 00:00:00'),'Fritz Müller','Lindenweg 15',
 '+436645989480','5020'),
(2,'Wolfsberg',TIMESTAMP('2014-02-14 00:00:00'),'Josef Berger','Eichenweg 131',
 '+436605684150','5010'),
(3,'Wels',TIMESTAMP('2015-08-09 00:00:00'),'Susanne Oberhauser','Aigen 4',
 '+436645989480','5020'),
(4,'Leonding',TIMESTAMP('2012-04-18 00:00:00'),'Manfred Friedwagner','Oftering 89',
 '+436645989480','5020'),
(5,'Steyr',TIMESTAMP('2016-01-17 00:00:00'),'David Schmidt','Gneisting 45b',
 '+436645989480','4801'),
(6,'Rohrbach',TIMESTAMP('2009-09-10 00:00:00'),'Alexander Weichselbaumer','Tulpenstraße 21a',
 '+436645989480','4790');

select * from App.CLIENT;
