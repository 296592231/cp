CREATE TABLE `ssq_base_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(10) DEFAULT NULL,
  `refernumber` varchar(30) DEFAULT NULL,
  `opendate` date DEFAULT NULL,
  `issueno` bigint(10) NOT NULL,
  `number` varchar(32) DEFAULT NULL,
  `saleamount` varchar(1024) DEFAULT NULL,
  `totalmoney` varchar(1024) DEFAULT NULL,
  `prize` varchar(2048) DEFAULT NULL,
  PRIMARY KEY (`id`,`issueno`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



CREATE TABLE `ssq_detail_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_user` varchar(32) DEFAULT NULL,
  `issueno` bigint(10) NOT NULL,
  `a_qiu` varchar(10) NOT NULL,
  `a_pianyi` varchar(10) DEFAULT NULL,
  `a_daxiao` varchar(10) DEFAULT NULL,
  `a_danshuang` varchar(10) DEFAULT NULL,
  `a_yushu` varchar(10) DEFAULT NULL,
  `a_wuxing` varchar(10) DEFAULT NULL,
  `b_qiu` varchar(10) NOT NULL,
  `b_pianyi` varchar(10) DEFAULT NULL,
  `b_daxiao` varchar(10) DEFAULT NULL,
  `b_danshuang` varchar(10) DEFAULT NULL,
  `b_yushu` varchar(10) DEFAULT NULL,
  `b_wuxing` varchar(10) DEFAULT NULL,
  `c_qiu` varchar(10) NOT NULL,
  `c_pianyi` varchar(10) DEFAULT NULL,
  `c_daxiao` varchar(10) DEFAULT NULL,
  `c_danshuang` varchar(10) DEFAULT NULL,
  `c_yushu` varchar(10) DEFAULT NULL,
  `c_wuxing` varchar(10) DEFAULT NULL,
  `d_qiu` varchar(10) NOT NULL,
  `d_pianyi` varchar(10) DEFAULT NULL,
  `d_daxiao` varchar(10) DEFAULT NULL,
  `d_danshuang` varchar(10) DEFAULT NULL,
  `d_yushu` varchar(10) DEFAULT NULL,
  `d_wuxing` varchar(10) DEFAULT NULL,
  `e_qiu` varchar(10) NOT NULL,
  `e_pianyi` varchar(10) DEFAULT NULL,
  `e_daxiao` varchar(10) DEFAULT NULL,
  `e_danshuang` varchar(10) DEFAULT NULL,
  `e_yushu` varchar(10) DEFAULT NULL,
  `e_wuxing` varchar(10) DEFAULT NULL,
  `f_qiu` varchar(10) NOT NULL,
  `f_pianyi` varchar(10) DEFAULT NULL,
  `f_daxiao` varchar(10) DEFAULT NULL,
  `f_danshuang` varchar(10) DEFAULT NULL,
  `f_yushu` varchar(10) DEFAULT NULL,
  `f_wuxing` varchar(10) DEFAULT NULL,
  `sum_value` bigint(10) DEFAULT NULL,
  `tail_sum_value` bigint(10) DEFAULT NULL,
  `san_section` varchar(20) DEFAULT NULL,
  `si_section` varchar(20) DEFAULT NULL,
  `qi_section` varchar(30) DEFAULT NULL,
  `daxiao_ratio` varchar(16) DEFAULT NULL,
  `danshuang_ratio` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`,`issueno`,`a_qiu`,`b_qiu`,`c_qiu`,`d_qiu`,`e_qiu`,`f_qiu`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `ssq_yu_ce` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `body` varchar(2048) DEFAULT NULL,
  `odds` varchar(1024) DEFAULT NULL,
  `isin` varchar(1024) DEFAULT NULL,
  `is_in_body` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;









