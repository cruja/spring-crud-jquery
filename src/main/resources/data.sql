insert into user (name, email, password, role, status) values ('cristi','cr@gmail.com','$2a$10$BsGIOoHyJv1BbJxRppWXvOPjfU21o6cI/xfxm6JdnF68MgMvBORWm','ADMIN', 'ACTIVE');
insert into user (name, email, password, role, status) values ('john','john@gmail.com','$2a$10$BsGIOoHyJv1BbJxRppWXvOPjfU21o6cI/xfxm6JdnF68MgMvBORWm','PUBLISHER', 'ACTIVE');
insert into user (name, email, password, role, status) values ('doe','doe@gmail.com','$2a$10$BsGIOoHyJv1BbJxRppWXvOPjfU21o6cI/xfxm6JdnF68MgMvBORWm','VIEWER', 'ACTIVE');

insert into publication (title, author, year, user_id) values ('hospital management for dummies','doctor1', 2010, 2);
insert into publication (title, author, year, user_id) values ('surgery professional','doctor2', 2016, 2);
insert into publication (title, author, year, user_id) values ('internal for dummies','doctor3', 2012, 3);

insert into subscription (type, user_id, publication_id) values ('MONTHLY',1, 3);
insert into subscription (type, user_id, publication_id) values ('YEARLY',1, 2);
insert into subscription (type, user_id, publication_id) values ('MONTHLY',3, 3);