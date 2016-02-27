insert into user (name, email, password, role, status) values ('admin','admin@gmail.com','$2a$10$BsGIOoHyJv1BbJxRppWXvOPjfU21o6cI/xfxm6JdnF68MgMvBORWm','ADMIN', 'ACTIVE');
insert into user (name, email, password, role, status) values ('publisher','publisher@gmail.com','$2a$10$BsGIOoHyJv1BbJxRppWXvOPjfU21o6cI/xfxm6JdnF68MgMvBORWm','PUBLISHER', 'ACTIVE');
insert into user (name, email, password, role, status) values ('viewer','viewer@gmail.com','$2a$10$BsGIOoHyJv1BbJxRppWXvOPjfU21o6cI/xfxm6JdnF68MgMvBORWm','VIEWER', 'ACTIVE');
insert into user (name, email, password, role, status) values ('pub2','pub2@gmail.com','$2a$10$BsGIOoHyJv1BbJxRppWXvOPjfU21o6cI/xfxm6JdnF68MgMvBORWm','PUBLISHER', 'ACTIVE');
insert into user (name, email, password, role, status) values ('view2','view2@gmail.com','$2a$10$BsGIOoHyJv1BbJxRppWXvOPjfU21o6cI/xfxm6JdnF68MgMvBORWm','VIEWER', 'ACTIVE');

insert into publication (title, author, year, user_id) values ('management for professional','doctor1', 2010, 2);
insert into publication (title, author, year, user_id) values ('learn surgery in a week','doctor2', 2016, 2);
insert into publication (title, author, year, user_id) values ('medical advices','doctor3', 2012, 4);

insert into subscription (type, user_id, publication_id) values ('MONTHLY',2, 1);
insert into subscription (type, user_id, publication_id) values ('YEARLY',3, 1);
insert into subscription (type, user_id, publication_id) values ('MONTHLY',3, 3);