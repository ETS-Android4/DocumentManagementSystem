create user 'admin_sistema'@'localhost' identified by 'admin_sistema';
grant select, insert, update, delete on sni_dms.user to 'admin_sistema'@'localhost';

create user 'user_dokumenti'@'localhost' identified by 'user_dokumenti';
grant select, insert on sni_dms.history_record to 'user_dokumenti'@'localhost';
grant select on sni_dms.user to 'user_dokumenti'@'localhost';
grant update (tokenExpiration) on sni_dms.user to 'user_dokumenti'@'localhost';
grant update (logoutTime) on sni_dms.user to 'user_dokumenti'@'localhost';

create user 'token_gen_app'@'localhost' identified by 'token_gen_app';
grant select on sni_dms.user to 'token_gen_app'@'localhost';
grant update (token, tokenExpiration) on sni_dms.user to 'token_gen_app'@'localhost';