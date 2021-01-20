SELECT DISTINCT U.fim_upc_no 
        FROM   land.imf AS I 
               LEFT OUTER JOIN land.imfc_fim_upc_no AS U 
                            ON I.isn = U.isn 
        WHERE (
        I.fric_major_dpt_no IN ( 1, 15, 20, 25, 30, 35, 40, 41, 45, 50)
        OR (I.fric_major_dpt_no = 60 and I.fric_minor_dpt_no in (7, 8, 9, 10, 14, 16, 22, 24))
        OR (I.fric_major_dpt_no = 67 and I.fric_minor_dpt_no in (21, 23))
              )
               AND I.isn IN (SELECT isn 
                             FROM   land.imfc_fim_rgn_high_use_tbl 
                             WHERE  fim_stk_stat IN ( 'A', 'E', 'G', 'J', 'P', 'S' ))