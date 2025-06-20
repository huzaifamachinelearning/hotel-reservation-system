CREATE TABLE guest(
guest_id NUMBER(15) PRIMARY KEY,
name VARCHAR2(100)   NOT NULL,
contact_no VARCHAR2(20) NOT NULL,
email VARCHAR2(100),
CONSTRAINT guest_contact_length CHECK( LENGTH(contact_no)>=7),
CONSTRAINT guest_email_at_sign  CHECK(email IS NULL OR INSTR(email,'@')>0)   );

CREATE TABLE recep(
recep_id NUMBER(6) PRIMARY KEY, 
name VARCHAR2(100) NOT NULL,
contact_no VARCHAR2(20) NOT NULL ,
email VARCHAR2(100) ,
password VARCHAR2(20) NOT NULL,
CONSTRAINT recep_contact_length CHECK( LENGTH(contact_no)>=7),
CONSTRAINT recep_email_at_sign  CHECK(email IS NULL OR INSTR(email,'@')>0) );

CREATE TABLE room_type(
room_type_id NUMBER(4) PRIMARY KEY,
type_name VARCHAR2(50) NOT NULL,
price_per_night NUMBER(6,2) NOT NULL ,
max_occupancy NUMBER(2) NOT NULL ,
bed_type VARCHAR2(20) NOT NULL ,
has_ac VARCHAR2(1),  
has_wifi VARCHAR2(1),
has_tv VARCHAR2(1),
CONSTRAINT price_positive CHECK(price_per_night>0),
CONSTRAINT type_name_unique UNIQUE (type_name),
CONSTRAINT has_ac_value CHECK( has_ac IN('y','n')),
CONSTRAINT has_wifi_value CHECK(has_wifi IN('y','n')),
CONSTRAINT has_tv_value CHECK(has_tv IN('y','n')) );

CREATE TABLE room (
room_id NUMBER(4) PRIMARY KEY,
room_type_id  NUMBER(4) NOT NULL ,
CONSTRAINT fk_room_type_id FOREIGN KEY (room_type_id) REFERENCES room_type(room_type_id) );
 

CREATE TABLE reserv(
reserv_id NUMBER(6) PRIMARY KEY,
guest_id NUMBER(15) NOT NULL ,
recep_id NUMBER(6) NOT NULL ,
advance_payment NUMBER(10,2)  DEFAULT 0 NOT NULL,
reserv_time DATE   DEFAULT SYSDATE NOT NULL,
check_in_date DATE NOT NULL,
check_out_date DATE  NOT NULL ,
reserv_status VARCHAR2(10)    NOT NULL,
CONSTRAINT advance_payment_positive CHECK(advance_payment>=0),
CONSTRAINT check_in_after_reserv CHECK(check_in_date>=reserv_time),
CONSTRAINT check_out_after_check_inn CHECK(check_out_date>=check_in_date),
CONSTRAINT fk_guest_id FOREIGN KEY (guest_id)  REFERENCES guest(guest_id),
CONSTRAINT fk_recep_id FOREIGN KEY (recep_id)  REFERENCES recep(recep_id)
 ); 

CREATE TABLE reserv_room (
reserv_id NUMBER(6) NOT NULL,
room_id NUMBER(4) NOT NULL,
PRIMARY KEY (reserv_id, room_id),
CONSTRAINT fk_rr_reserv_id FOREIGN KEY (reserv_id) REFERENCES reserv(reserv_id),
CONSTRAINT fk_rr_room_id FOREIGN KEY (room_id) REFERENCES room(room_id)
);


CREATE TABLE bill(
bill_id NUMBER(6) PRIMARY KEY,
reserv_id NUMBER(6) NOT NULL ,
amount NUMBER(10,2) NOT NULL ,
payment_status VARCHAR2(10)  DEFAULT 'pending' NOT NULL ,
payment_date DATE,
CONSTRAINT amount_positive CHECK(amount>0),
CONSTRAINT fk_reserv_id FOREIGN KEY (reserv_id) REFERENCES reserv(reserv_id),
CONSTRAINT unique_reserv_in_bill UNIQUE (reserv_id)  ); 




 









