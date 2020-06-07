CREATE TABLE coupon
(
  coupon_srl BIGINT(16) AUTO_INCREMENT PRIMARY KEY,
  coupon_key VARCHAR(30) NOT NULL,
  create_date DATE NOT NULL,
  user VARCHAR(20),
  issue_date DATE,
  use_date DATE,
  start_date DATE,
  end_date DATE,
  used_date DATE,
  used BOOLEAN NOT NULL DEFAULT FALSE,
  key (coupon_key, used),
  key (issue_date),
  key (end_date, used)
);
