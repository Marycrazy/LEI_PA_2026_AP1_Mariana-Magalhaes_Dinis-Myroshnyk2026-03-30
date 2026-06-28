-- ------------------------------
-- OPTION
-- ------------------------------

OPTION IMPORT;

-- ------------------------------
-- FUNCTIONS
-- ------------------------------

DEFINE FUNCTION fn::check_email($input: string) -> bool { IF !string::is_email($input) { THROW 'Invalid email format' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_nif($input: string) -> bool { IF $input != /^[1-9]\d{8}$/ { THROW 'Malformed NIF' }; IF (SELECT id FROM registrable_user WHERE nif = $input)[0] { THROW 'NIF already exists' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_phone($input: string) -> bool { IF $input != /^[923]\d{8}$/ { THROW 'Invalid phone number' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_scale($input: string) -> bool { IF $input NOTINSIDE ['A', 'B', 'C', 'D'] { THROW 'Scale not of value [A, B, C, D]' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_sku($input: int) -> bool { IF (SELECT id FROM equipment WHERE sku = $input)[0] { THROW 'SKU already registered' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_specialization($input: string) -> bool { IF $input NOTINSIDE ['1', '2', '3', '4', '5'] { THROW 'Scale not of value [1, 2, 3, 4, 5]' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::check_username($input: string) -> bool { IF (SELECT id FROM user WHERE username = $input)[0] { THROW 'Username is already taken' }; RETURN true; } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::generate_repair_code() -> string { LET $count = array::len((SELECT id FROM repair)); LET $next = $count + 1; LET $now = time::now(); LET $year = type::string(time::year($now)); LET $month = (IF time::month($now) < 10 { '0' + type::string(time::month($now)) } ELSE { type::string(time::month($now)) }); LET $day = (IF time::day($now) < 10 { '0' + type::string(time::day($now)) } ELSE { type::string(time::day($now)) }); LET $hour = (IF time::hour($now) < 10 { '0' + type::string(time::hour($now)) } ELSE { type::string(time::hour($now)) }); LET $minute = (IF time::minute($now) < 10 { '0' + type::string(time::minute($now)) } ELSE { type::string(time::minute($now)) }); LET $second = (IF time::second($now) < 10 { '0' + type::string(time::second($now)) } ELSE { type::string(time::second($now)) }); LET $date = $year + $month + $day + $hour + $minute + $second; RETURN type::string($next) + $date; } PERMISSIONS FULL;
DEFINE FUNCTION fn::get_user_type($user: string) -> string { RETURN (SELECT VALUE type FROM user WHERE username = $user)[0] } COMMENT '' PERMISSIONS FULL;
DEFINE FUNCTION fn::user_exists($user: string, $pass: string) -> bool { IF (SELECT id FROM user WHERE username = $user AND password = $pass)[0] { RETURN true }; RETURN false; } COMMENT '' PERMISSIONS FULL;

-- ------------------------------
-- TABLE: client
-- ------------------------------

DEFINE TABLE client TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD scale ON client TYPE string ASSERT $value INSIDE ['A', 'B', 'C', 'D'] PERMISSIONS FULL;
DEFINE FIELD sector ON client TYPE string PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: client
-- ------------------------------

INSERT [ { id: client:69tioww139l2aj6697bb, scale: 'A', sector: 'cba' }, { id: client:8h9eyqmx7ott54xn286m, scale: 'C', sector: 'º_º' }, { id: client:bmw2nfd8c4j6zl3sg0ht, scale: 'B', sector: 'something here !!!' }, { id: client:zc2mfyvyp5aghg0m4c1v, scale: 'A', sector: 'abc' } ];

-- ------------------------------
-- TABLE: contains
-- ------------------------------

DEFINE TABLE contains TYPE RELATION IN equipment OUT repair SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON contains TYPE record<equipment> PERMISSIONS FULL;
DEFINE FIELD out ON contains TYPE record<repair> PERMISSIONS FULL;


DEFINE EVENT update_equipment_submission ON contains WHEN $event = 'CREATE' THEN { UPDATE $after.in SET last_submission_date = time::now() };

-- ------------------------------
-- TABLE DATA: contains
-- ------------------------------

INSERT RELATION [ { id: contains:0f89xtggbnnsjp9h6sx8, in: equipment:leh5yfmi8hrdi83l09gp, out: repair:lb19dtyzlp9qfdgoqltb }, { id: contains:0owgl8b53dxcu0685w6l, in: equipment:leh5yfmi8hrdi83l09gp, out: repair:lt367ml0igxscnoi4egz }, { id: contains:rnhn4h3626fiin9mycr2, in: equipment:leh5yfmi8hrdi83l09gp, out: repair:rep13 }, { id: contains:w7arg7mxgsuo1c9r3m7y, in: equipment:leh5yfmi8hrdi83l09gp, out: repair:vxbtnpig622ojslypzc9 } ];

-- ------------------------------
-- TABLE: employee
-- ------------------------------

DEFINE TABLE employee TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD specialization ON employee TYPE string ASSERT $value INSIDE ['1', '2', '3', '4', '5'] PERMISSIONS FULL;
DEFINE FIELD start_date ON employee TYPE datetime DEFAULT time::now() PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: employee
-- ------------------------------

INSERT [ { id: employee:9jps5xgaeb4emtjv4auj, specialization: '2', start_date: d'2026-04-18T14:32:15.711017030Z' }, { id: employee:fxok69xuku141w4ozdqh, specialization: '1', start_date: d'2026-06-02T14:42:13.633104060Z' }, { id: employee:v3aohn2re21f56hvjjnm, specialization: '5', start_date: d'2026-06-26T13:58:06.394041120Z' } ];

-- ------------------------------
-- TABLE: equipment
-- ------------------------------

DEFINE TABLE equipment TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD batch ON equipment TYPE string PERMISSIONS FULL;
DEFINE FIELD brand ON equipment TYPE string PERMISSIONS FULL;
DEFINE FIELD last_repair_date ON equipment TYPE none | datetime PERMISSIONS FULL;
DEFINE FIELD last_submission_date ON equipment TYPE none | datetime DEFAULT NONE PERMISSIONS FULL;
DEFINE FIELD manufacturing_date ON equipment TYPE none | datetime DEFAULT NONE PERMISSIONS FULL;
DEFINE FIELD model ON equipment TYPE string PERMISSIONS FULL;
DEFINE FIELD sku ON equipment TYPE int DEFAULT `rand`::int(1, 1000000) PERMISSIONS FULL;

DEFINE INDEX unique_sku ON equipment FIELDS sku UNIQUE;


-- ------------------------------
-- TABLE DATA: equipment
-- ------------------------------

INSERT [ { batch: '13', brand: 'BLABLA', id: equipment:leh5yfmi8hrdi83l09gp, last_repair_date: d'2026-06-28T00:33:05.042259547Z', last_submission_date: d'2026-06-28T00:33:05.101657183Z', manufacturing_date: d'1999-02-04T00:00:00Z', model: 'M3', sku: 584739 } ];

-- ------------------------------
-- TABLE: has
-- ------------------------------

DEFINE TABLE has TYPE RELATION IN repair OUT test SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON has TYPE record<repair> PERMISSIONS FULL;
DEFINE FIELD out ON has TYPE record<test> PERMISSIONS FULL;


DEFINE EVENT log_test_create ON has WHEN $event = 'CREATE' THEN { LET $user = (SELECT in FROM user_repair WHERE out = $after.in)[0].in; CREATE log SET action = 'INSERT', created_at = time::now(), details = string::concat("Test '", type::string($after.out), "' was added to repair '", $after.in, "'."), user = $user; };

-- ------------------------------
-- TABLE DATA: has
-- ------------------------------


-- ------------------------------
-- TABLE: inserts
-- ------------------------------

DEFINE TABLE inserts TYPE RELATION IN client OUT equipment SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON inserts TYPE record<client> PERMISSIONS FULL;
DEFINE FIELD out ON inserts TYPE record<equipment> PERMISSIONS FULL;


DEFINE EVENT log_equipment_create ON inserts WHEN $event = 'CREATE' THEN { LET $registrable = (SELECT in FROM of_type WHERE out = $after.in)[0].in; LET $user = (SELECT in FROM is_a WHERE out = $registrable)[0].in; LET $equipment = (SELECT brand, model FROM equipment WHERE id = $after.out)[0]; CREATE log SET action = 'INSERT', created_at = time::now(), details = string::concat("Equipment brand '", $equipment.brand, "' and model '", $equipment.model, "' was added to the system."), user = $user; };

-- ------------------------------
-- TABLE DATA: inserts
-- ------------------------------

INSERT RELATION [ { id: inserts:20mmms5x7zpnysnlz509, in: client:8h9eyqmx7ott54xn286m, out: equipment:leh5yfmi8hrdi83l09gp } ];

-- ------------------------------
-- TABLE: is_a
-- ------------------------------

DEFINE TABLE is_a TYPE RELATION IN user OUT registrable_user SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON is_a TYPE record<user> PERMISSIONS FULL;
DEFINE FIELD out ON is_a TYPE record<registrable_user> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: is_a
-- ------------------------------

INSERT RELATION [ { id: is_a:0v8kuovu19zilt1yp84v, in: user:81b7mgqv0p07m4vo5tda, out: registrable_user:5ta0hfxcy4jsqwyepaef }, { id: is_a:l509n8i0159x3fws40nl, in: user:mjpplilcnwj0wi8p9i5u, out: registrable_user:sr4esbzl89uuadgbu7at }, { id: is_a:rlmurxd0kb16y5npqvj6, in: user:x8t7ipszhpkfvyuk3x8u, out: registrable_user:wbaf52pgvsk2sobpz78l }, { id: is_a:rp6rn79kwjy4iivlc2v0, in: user:au3s6lse81nckxrfaqm5, out: registrable_user:9q7iyg29v2s2te702zs8 }, { id: is_a:wjmxj5itckicnpx84670, in: user:wn7cijtudomcji7c4akx, out: registrable_user:60fiqt3b6g3k34qfi66t }, { id: is_a:ypimjh3h66uybxktcjsi, in: user:7cn3sw8qstj5n7zjw8lf, out: registrable_user:p1re34oq2ffxo4wupkhy }, { id: is_a:zat4aiep9n84o1c1lnne, in: user:y3ea297c5cwk9d30k7aa, out: registrable_user:zipcsugqkunrf8vu06cv } ];

-- ------------------------------
-- TABLE: log
-- ------------------------------

DEFINE TABLE log TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD action ON log TYPE string PERMISSIONS FULL;
DEFINE FIELD created_at ON log TYPE datetime PERMISSIONS FULL;
DEFINE FIELD details ON log TYPE string PERMISSIONS FULL;
DEFINE FIELD user ON log TYPE record<user> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: log
-- ------------------------------

INSERT [ { action: 'UPDATE', created_at: d'2026-04-18T17:24:32.047273887Z', details: "User updated. Changes: name: 'testeeeeee'->'BlaBla Teste'", id: log:05od89c7jhz805qz3nbp, user: user:au3s6lse81nckxrfaqm5 }, { action: 'INSERT', created_at: d'2026-04-18T14:08:12.685717360Z', details: "Equipment 'equipment:eq1' was added to the system.", id: log:0cn8nn8odug21ynfpvll, user: user:y3ea297c5cwk9d30k7aa }, { action: 'UPDATE', created_at: d'2026-06-28T03:49:55.184097584Z', details: "User updated. Changes: name: 'Estou cansado !!!'->'Estou cansadoooooo', email: 'fsdfdsf@asd.asdf'->'fsdfdsf@asd.aasdf'", id: log:30cutxwyf53ogzr8ywyn, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-04-18T20:15:35.240011234Z', details: "Repair 'repair:rep13' changed state to 'NONE' by 'Test Client'.", id: log:4gjkkhtimroey427a4x8, user: user:y3ea297c5cwk9d30k7aa }, { action: 'UPDATE', created_at: d'2026-06-28T03:34:11.868475619Z', details: "User updated. Changes: name: 'Estou cansado'->'Estou cansado !!!'", id: log:4uawc9r1atrxcgaq0ssn, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'INSERT', created_at: d'2026-04-18T16:11:08.147976421Z', details: "Test 'test:test0' was added to repair 'repair:rep11'.", id: log:5l3vlquab48q9y2vksjz, user: user:y3ea297c5cwk9d30k7aa }, { action: 'UPDATE', created_at: d'2026-06-28T03:34:59.632802136Z', details: "User updated. Changes: name: 'Estou cansado !!!'->'Estou cansado'", id: log:5uyguxd0tijzoctjgmcx, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-28T03:43:26.851467541Z', details: "User updated. Changes: name: 'Estou cansado'->'Estou cansado !!!'", id: log:5viscbwdyxy9fvh1l3rx, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'INSERT', created_at: d'2026-06-26T13:58:06.204291958Z', details: "User 'moodeng' (EMPLOYEE) was registered in the system.", id: log:6lnt4cjj92ax39zvevtt, user: user:81b7mgqv0p07m4vo5tda }, { action: 'UPDATE', created_at: d'2026-04-18T16:38:16.418821895Z', details: "User updated. Changes: name: 'Testeeeee'->'Testeeeeeeee2', status: 'ACTIVE' -> 'PENDING'", id: log:6vrfm6orrlnfm8cg5vf6, user: user:au3s6lse81nckxrfaqm5 }, { action: 'UPDATE', created_at: d'2026-06-28T00:39:43.773096290Z', details: "Repair 'repair:lt367ml0igxscnoi4egz' changed state to 'ACCEPTED' by 'Teste employee'.", id: log:76eu2t2vqv5r5u0suggk, user: user:wn7cijtudomcji7c4akx }, { action: 'UPDATE', created_at: d'2026-06-27T02:38:06.202092233Z', details: 'User updated. Changes: ', id: log:78q0yclo0jdw17jv2r29, user: user:au3s6lse81nckxrfaqm5 }, { action: 'UPDATE', created_at: d'2026-06-27T02:37:47.799460500Z', details: 'User updated. Changes: ', id: log:8ch5e7i2xiuxa4t9d37a, user: user:wx3rb1hmzootjuhnrdur }, { action: 'INSERT', created_at: d'2026-04-18T16:57:35.150392743Z', details: "Repair request 'repair:rep13' was submitted by 'Test Client'.", id: log:9urafbnf8y0d0r1aja2r, user: user:y3ea297c5cwk9d30k7aa }, { action: 'INSERT', created_at: d'2026-04-18T15:50:56.018471091Z', details: "Repair request 'repair:rep11' was submitted by 'Test Client'.", id: log:ack28fei22o5vp01fu9h, user: user:y3ea297c5cwk9d30k7aa }, { action: 'INSERT', created_at: d'2026-04-18T20:02:35.378680225Z', details: "Equipment brand 'BLABLA' and model 'M3' was added to the system.", id: log:b0h7cayxot1z43jg45cv, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-02T16:08:14.486073529Z', details: "Repair 'repair:lb19dtyzlp9qfdgoqltb' changed state to 'ACCEPTED' by 'BlaBla Teste'.", id: log:b8esdf6cj2gs9u7p4ofs, user: user:au3s6lse81nckxrfaqm5 }, { action: 'UPDATE', created_at: d'2026-06-03T11:24:33.347070033Z', details: "Repair 'repair:lb19dtyzlp9qfdgoqltb' changed state to 'ACCEPTED' by 'Teste employee'.", id: log:cr7fe2k4kv1k5wobmxk8, user: user:wn7cijtudomcji7c4akx }, { action: 'UPDATE', created_at: d'2026-06-27T02:37:58.856913105Z', details: 'User updated. Changes: ', id: log:cvkugfsyggsw7jx6kvcu, user: user:mjpplilcnwj0wi8p9i5u }, { action: 'UPDATE', created_at: d'2026-06-28T02:26:00.801458751Z', details: "User updated. Changes: name: 'Test 54321'->'Test 54321000'", id: log:eoeb6vx1rmkmmg5w6ebb, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-28T00:49:52.635770775Z', details: "Repair 'repair:lt367ml0igxscnoi4egz' changed state to 'ACCEPTED' by 'Moo Deng'.", id: log:fe91vl5h3cimjv5cfxkc, user: user:81b7mgqv0p07m4vo5tda }, { action: 'INSERT', created_at: d'2026-04-18T20:14:51.914388837Z', details: "Repair request 'repair:rep13' was submitted by 'Test Client'.", id: log:fjhd1pde98fb74wkybj6, user: user:y3ea297c5cwk9d30k7aa }, { action: 'INSERT', created_at: d'2026-04-18T20:53:44.796377671Z', details: "Repair request 'repair:rep13' was submitted by 'Test Client'.", id: log:h61tkzkk9x2fgw92qcqt, user: user:y3ea297c5cwk9d30k7aa }, { action: 'INSERT', created_at: d'2026-04-18T14:32:15.509111534Z', details: "User 'employee' (EMPLOYEE) was registered in the system.", id: log:hgufqj5xezfj5g1wvid1, user: user:au3s6lse81nckxrfaqm5 }, { action: 'UPDATE', created_at: d'2026-06-02T19:57:42.801619439Z', details: "User updated. Changes: status: 'PENDING' -> 'ACTIVE'", id: log:iaqj2vvrw00lhmytk1s0, user: user:y3ea297c5cwk9d30k7aa }, { action: 'UPDATE', created_at: d'2026-06-02T19:22:19.990711939Z', details: "User updated. Changes: name: 'BlaBla Teste'->'employee teste1'", id: log:jb1w3vl84er833whpddh, user: user:au3s6lse81nckxrfaqm5 }, { action: 'INSERT', created_at: d'2026-06-27T22:31:36.835604326Z', details: "Repair request 'repair:vxbtnpig622ojslypzc9' was submitted by 'Test 12345'.", id: log:jsdgklb8dsb7i55cpl4h, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-28T01:50:39.754173167Z', details: 'User updated. Changes: ', id: log:kfr5ep68df7t70rt2tbn, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-28T03:31:23.741258291Z', details: "User updated. Changes: name: 'Test 54321002'->'Estou cansado'", id: log:llthqiqsz90ybj8vq7gz, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-04-18T16:57:50.418569463Z', details: "Repair 'repair:rep13' changed state to 'IN_PROGRESS' by 'Admin'.", id: log:ltczdx5rqe7wr8st29bz, user: user:3xn26pscybryt47nhopo }, { action: 'UPDATE', created_at: d'2026-06-28T02:05:53.434758641Z', details: "User updated. Changes: name: 'Test 12345'->'Test 54321'", id: log:lzq1rhd1pblrzq04ir6m, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-08T15:30:18.108739221Z', details: "Repair 'repair:lb19dtyzlp9qfdgoqltb' changed state to 'ACCEPTED' by 'employee teste1'.", id: log:mcaniwpb321tc8oje22u, user: user:au3s6lse81nckxrfaqm5 }, { action: 'INSERT', created_at: d'2026-04-18T15:50:25.716677346Z', details: "Equipment brand 'Samsung' and model 'Galaxy Tab' was added to the system.", id: log:n1exflviwc75sxeiyyew, user: user:y3ea297c5cwk9d30k7aa }, { action: 'UPDATE', created_at: d'2026-04-18T17:19:37.154226406Z', details: "User updated. Changes: name: 'Testeeeeeeee2'->'testeeeeee'", id: log:nrmopwsfr3lxzitxgyyi, user: user:au3s6lse81nckxrfaqm5 }, { action: 'UPDATE', created_at: d'2026-06-28T03:07:13.583391216Z', details: "User updated. Changes: name: 'Test 54321001'->'Test 54321002'", id: log:o149z2xvjwurvoexlrzr, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-27T02:37:53.384277904Z', details: 'User updated. Changes: ', id: log:o6jzi3he2rwpi4yy3yb0, user: user:wn7cijtudomcji7c4akx }, { action: 'UPDATE', created_at: d'2026-06-28T03:49:25.182455331Z', details: "User updated. Changes: email: 'fsdfdsf@asd.asd'->'fsdfdsf@asd.asdf'", id: log:onvllhsgdmn1lnppfkg1, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-28T04:07:50.350468495Z', details: "User updated. Changes: name: 'Test Client'->'Test Client I'", id: log:orqdj2rkgobprphgufc8, user: user:y3ea297c5cwk9d30k7aa }, { action: 'INSERT', created_at: d'2026-06-26T02:57:37.534771669Z', details: "User 'testclient' (CLIENT) was registered in the system.", id: log:prwmn7hye53nsbwfp9e0, user: user:7cn3sw8qstj5n7zjw8lf }, { action: 'UPDATE', created_at: d'2026-06-28T04:06:55.432232930Z', details: 'User updated. Changes: ', id: log:qm9jszkxfg4wudulfotv, user: user:wn7cijtudomcji7c4akx }, { action: 'INSERT', created_at: d'2026-06-28T00:33:05.164275483Z', details: "Repair request 'repair:lt367ml0igxscnoi4egz' was submitted by 'Test 12345'.", id: log:sa3w9yrufdbobis2647s, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'INSERT', created_at: d'2026-06-08T22:43:02.177413650Z', details: "User 'blabla02' (CLIENT) was registered in the system.", id: log:sxccy78v0yq34nkviwqk, user: user:mjpplilcnwj0wi8p9i5u }, { action: 'UPDATE', created_at: d'2026-06-28T02:27:28.741313747Z', details: "User updated. Changes: name: 'Test 54321000'->'Test 54321001'", id: log:t54k4djfn3xbeeksa1b5, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-27T02:37:42.054460197Z', details: 'User updated. Changes: ', id: log:to7a9zzx99coltv5rzqm, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'INSERT', created_at: d'2026-06-02T14:42:13.484163011Z', details: "User 'emp' (EMPLOYEE) was registered in the system.", id: log:u26cwybmargjku027ob0, user: user:wn7cijtudomcji7c4akx }, { action: 'INSERT', created_at: d'2026-04-18T20:55:31.034732220Z', details: "Repair request 'repair:lb19dtyzlp9qfdgoqltb' was submitted by 'Test 12345'.", id: log:v1nirfsapbnckznojum8, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'INSERT', created_at: d'2026-06-08T22:14:44.851315335Z', details: "User 'blabla02' (CLIENT) was registered in the system.", id: log:v242b05kvchpsbgjn1l4, user: user:2d6gwoa9mtuz43bh7cun }, { action: 'UPDATE', created_at: d'2026-04-18T20:17:40.305511081Z', details: "Repair 'repair:rep13' changed state to 'NONE' by 'Test Client'.", id: log:v2oa2uoc786wl4q4s5aw, user: user:y3ea297c5cwk9d30k7aa }, { action: 'UPDATE', created_at: d'2026-06-02T14:39:53.937238720Z', details: "User updated. Changes: status: 'PENDING' -> 'ACTIVE'", id: log:v3r9i3dnbbw1f92mfkio, user: user:au3s6lse81nckxrfaqm5 }, { action: 'UPDATE', created_at: d'2026-06-27T02:36:21.423203576Z', details: 'User updated. Changes: ', id: log:v5vn8557ijoaoxm57toh, user: user:y3ea297c5cwk9d30k7aa }, { action: 'UPDATE', created_at: d'2026-04-18T20:18:27.773589126Z', details: "Repair 'repair:rep13' changed state to 'PENDING' by 'Test Client'.", id: log:vaidrs5zggcppy90ppy4, user: user:y3ea297c5cwk9d30k7aa }, { action: 'UPDATE', created_at: d'2026-06-28T04:03:42.759919986Z', details: "User updated. Changes: name: 'Estou cansadoooooo'->'Estou cansado', email: 'fsdfdsf@asd.aasdf'->'fsdfdsf@asd.aasafqeqeeee'", id: log:vp2gdkyv57pmz8sosdzk, user: user:x8t7ipszhpkfvyuk3x8u }, { action: 'UPDATE', created_at: d'2026-06-27T02:38:11.510999105Z', details: 'User updated. Changes: ', id: log:vpo0a4i2c6rh4icqdxtz, user: user:3xn26pscybryt47nhopo }, { action: 'INSERT', created_at: d'2026-06-08T22:26:43.852893441Z', details: "User 'blabla02' (CLIENT) was registered in the system.", id: log:wg3yfx9kkib3iz9xt33i, user: user:q85zz957avxdyng8xvxx }, { action: 'UPDATE', created_at: d'2026-06-27T01:49:32.084081950Z', details: "User updated. Changes: status: 'PENDING' -> 'ACTIVE'", id: log:wtqcpm0mabxch3y41z8n, user: user:81b7mgqv0p07m4vo5tda }, { action: 'INSERT', created_at: d'2026-04-18T14:31:37.295777197Z', details: "User 'admin2' (ADMIN) was registered in the system.", id: log:wx0nxasajrafbpisdbn3, user: user:wx3rb1hmzootjuhnrdur }, { action: 'INSERT', created_at: d'2026-04-18T13:41:03.329317029Z', details: "User 'blabla' (CLIENT) was registered in the system.", id: log:zt9903si29blfamqe8s2, user: user:x8t7ipszhpkfvyuk3x8u } ];

-- ------------------------------
-- TABLE: notification
-- ------------------------------

DEFINE TABLE notification TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD content ON notification TYPE string PERMISSIONS FULL;
DEFINE FIELD created_at ON notification TYPE datetime DEFAULT time::now() PERMISSIONS FULL;
DEFINE FIELD target ON notification TYPE record | string PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: notification
-- ------------------------------

INSERT [ { content: "User 'testclient' awaiting approval", created_at: d'2026-06-26T02:57:39.949884838Z', id: notification:1a922xr2dnehgxpf5ixz, target: 'ADMIN' }, { content: 'You have been assigned repair 120260418205530', created_at: d'2026-06-08T15:30:18.258195731Z', id: notification:5uztsldscnxddj1c5t10, target: user:au3s6lse81nckxrfaqm5 }, { content: 'Your repair request 120260418205530 was marked as completed.', created_at: d'2026-06-08T15:31:01.564256367Z', id: notification:7kfulb4tdbrgifvqa6o4, target: user:x8t7ipszhpkfvyuk3x8u }, { content: 'Your repair request 220260627223136 was rejected by the admin. Reason: nao possui os requesitos', created_at: d'2026-06-27T22:54:56.665959876Z', id: notification:a6ft05v8zgzjff8o7cop, target: user:x8t7ipszhpkfvyuk3x8u }, { content: "User 'blabla02' awaiting approval", created_at: d'2026-06-08T22:14:45.291198843Z', id: notification:d9tqpgiaangbo8m8hr4h, target: 'ADMIN' }, { content: 'New repair request submitted by Test 12345', created_at: d'2026-04-18T20:55:31.220227390Z', id: notification:g3z0r1bzhxlldd45ujl5, target: 'ADMIN' }, { content: 'Your repair request 120260418205530 was marked as completed.', created_at: d'2026-06-03T11:20:50.853447734Z', id: notification:hnoxlie77ew4gprorn0t, target: user:x8t7ipszhpkfvyuk3x8u }, { content: 'New repair request submitted by Test 12345', created_at: d'2026-06-27T22:31:37.013806297Z', id: notification:i732d1fegnql4w577jnq, target: 'ADMIN' }, { content: 'Your repair request 120260418205530 was rejected by the admin. Reason: nao fa�o ideia do porque', created_at: d'2026-06-03T11:22:55.373566543Z', id: notification:ii0hudk6sdu8p4m23mez, target: user:x8t7ipszhpkfvyuk3x8u }, { content: 'New repair request submitted by Test 12345', created_at: d'2026-06-28T00:33:05.401207182Z', id: notification:jopds47opm18pm1f4l3f, target: 'ADMIN' }, { content: "User 'blabla02' awaiting approval", created_at: d'2026-06-08T22:26:44.294727099Z', id: notification:lkqcnqhnp06bbn1qlixl, target: 'ADMIN' }, { content: 'You have been assigned repair 320260628003305', created_at: d'2026-06-28T00:49:52.813915564Z', id: notification:on2zxxhevfr6tsonnlgl, target: user:81b7mgqv0p07m4vo5tda }, { content: "User 'client' awaiting approval", created_at: d'2026-04-16T21:33:30.348484060Z', id: notification:p6indc5kfodbkfs9iba5, target: 'ADMIN' }, { content: "This is a very very looooong error message that should be wrapped to fit within the box, but if it reaches 18 lines, it should be truncated to 17 lines with a 'truncated' message at the very end. This test message should be simulating a realy long error report so i can test the wrapping and truncation functionalities, as well as the clipboard copy through OSC 52 and the file falback if the terminal doesn't support it. As the message up to this point has not yet reached the maximum allowed height, i should be writing something else but i really am running out of ideas. Just remembered that i should be testing LongLinesWithoutAnySpacesinBetweenThemToReallyStressTestTheWrappingFuntionality. I'll also throw a link here just because: 'https://puginarug.com/'. Now i really ran out of ideas so from this point onwards, i will paste a text generated by our overlord, ChatGPT: (in a robotic voice) 'Sure — here's a natural continuation that keeps the same tone and purpose, and should help push the message over the line limit for testing wrapping and truncation: ...At this stage, the content is mostly filler, but that's intentional, because the goal is not readability so much as stress-testing how the UI behaves under pressure. Ideally, the text should wrap cleanly at word boundaries, avoid breaking escape sequences, and remain visually consistent across different terminal widths and font settings. If everything works as expected, the final visible line should clearly indicate that truncation has occurred, without cutting off mid-word or corrupting the output. And if none of that happens, well, at least this overly verbose message served its purpose as a reliable worst-case scenario.'. And yet, HIS machine-like words where not enough to fill the text box. Or where they ? (Moon Men by Jake Chudnow starts playing as Michael menacingly appears behind you). I don't really know as i've yet to run the program with message. I was never good at wrapping up my texts so, whatever, whatever, thank you for being at my TED Talk. I'm going to go now. Bye.", created_at: d'2026-04-17T00:54:11.689454574Z', id: notification:pgmay7d8hgowfz84jf4s, target: 'ADMIN' }, { content: 'Test notification', created_at: d'2026-04-16T22:49:58.004883317Z', id: notification:qtbkmvazc4v2ltunrs4u, target: user:3xn26pscybryt47nhopo }, { content: "User 'moodeng' awaiting approval", created_at: d'2026-06-26T13:58:08.778710824Z', id: notification:qxlen7b1z28wv6hpxk79, target: 'ADMIN' }, { content: 'You have been assigned repair 120260418205530', created_at: d'2026-06-02T16:08:14.992704637Z', id: notification:qy4az1iu57ojm04goh0s, target: user:au3s6lse81nckxrfaqm5 }, { content: 'Your repair request 120260418205530 was marked as completed.', created_at: d'2026-06-02T23:37:11.093870786Z', id: notification:r6y2orikks6ft6wwwqjn, target: user:au3s6lse81nckxrfaqm5 }, { content: 'You have been assigned repair 120260418205530', created_at: d'2026-06-03T11:24:33.503653169Z', id: notification:tyhor5gidaomghrk8epo, target: user:wn7cijtudomcji7c4akx }, { content: 'Your repair request 320260628003305 was rejected by the assigned employee. Reason: porque posso', created_at: d'2026-06-28T00:42:41.197096311Z', id: notification:xrb8ps4mon1wfh7rfyut, target: 'ADMIN' }, { content: 'You have been assigned repair 320260628003305', created_at: d'2026-06-28T00:39:43.950559313Z', id: notification:xzxxbavm9j8ay4poh9oh, target: user:wn7cijtudomcji7c4akx }, { content: "User 'blabla02' awaiting approval", created_at: d'2026-06-08T22:43:02.655063031Z', id: notification:yr32hq9efdp82t2o8ed1, target: 'ADMIN' }, { content: 'Your repair request 120260418205530 was rejected by the assigned employee. Reason: nao sei', created_at: d'2026-06-03T11:25:20.522675512Z', id: notification:yyhlp9hv47i7yhe0v6dm, target: 'ADMIN' } ];

-- ------------------------------
-- TABLE: of_type
-- ------------------------------

DEFINE TABLE of_type TYPE RELATION IN registrable_user OUT client | employee SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON of_type TYPE record<registrable_user> PERMISSIONS FULL;
DEFINE FIELD out ON of_type TYPE record<client | employee> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: of_type
-- ------------------------------

INSERT RELATION [ { id: of_type:0or51pfssyd6blqlx72j, in: registrable_user:9q7iyg29v2s2te702zs8, out: employee:9jps5xgaeb4emtjv4auj }, { id: of_type:0x7decoms3xr7158wmfj, in: registrable_user:wbaf52pgvsk2sobpz78l, out: client:8h9eyqmx7ott54xn286m }, { id: of_type:3rdlikcj08zqojb959jw, in: registrable_user:p1re34oq2ffxo4wupkhy, out: client:bmw2nfd8c4j6zl3sg0ht }, { id: of_type:52jion0r82vcc3j72vp6, in: registrable_user:5ta0hfxcy4jsqwyepaef, out: employee:v3aohn2re21f56hvjjnm }, { id: of_type:8eoi8e9bgtprk1wovhgl, in: registrable_user:sr4esbzl89uuadgbu7at, out: client:69tioww139l2aj6697bb }, { id: of_type:aic24uaqu61fgb2htx6t, in: registrable_user:60fiqt3b6g3k34qfi66t, out: employee:fxok69xuku141w4ozdqh }, { id: of_type:nebih1vn6d64xkugxmtj, in: registrable_user:zipcsugqkunrf8vu06cv, out: client:zc2mfyvyp5aghg0m4c1v } ];

-- ------------------------------
-- TABLE: part
-- ------------------------------

DEFINE TABLE part TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD designation ON part TYPE string PERMISSIONS FULL;
DEFINE FIELD manufacturer ON part TYPE string PERMISSIONS FULL;
DEFINE FIELD stock_quantity ON part TYPE int PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: part
-- ------------------------------

INSERT [ { designation: 'This is a test 2', id: part:f03f1u126nh0ghxn74eu, manufacturer: 'BLABLA', stock_quantity: 275 }, { designation: 'Something something', id: part:f82syd0c9jtnuhdrcnmo, manufacturer: 'BLABLA', stock_quantity: 125 } ];

-- ------------------------------
-- TABLE: registrable_user
-- ------------------------------

DEFINE TABLE registrable_user TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD address ON registrable_user TYPE string PERMISSIONS FULL;
DEFINE FIELD nif ON registrable_user TYPE string ASSERT $value = /^[1-9]\d{8}$/ PERMISSIONS FULL;
DEFINE FIELD phone ON registrable_user TYPE string ASSERT $value = /^[923]\d{8}$/ PERMISSIONS FULL;

DEFINE INDEX unique_nif ON registrable_user FIELDS nif UNIQUE;


-- ------------------------------
-- TABLE DATA: registrable_user
-- ------------------------------

INSERT [ { address: 'Khao Kheow Zoo', id: registrable_user:5ta0hfxcy4jsqwyepaef, nif: '273611128', phone: '929999938' }, { address: 'rua das flores', id: registrable_user:60fiqt3b6g3k34qfi66t, nif: '211326688', phone: '919394765' }, { address: 'Test Address', id: registrable_user:9q7iyg29v2s2te702zs8, nif: '271626372', phone: '923715385' }, { address: 'BLABLABLBALABAA', id: registrable_user:p1re34oq2ffxo4wupkhy, nif: '273841623', phone: '928374103' }, { address: 'This is a TEST address.', id: registrable_user:sr4esbzl89uuadgbu7at, nif: '271649324', phone: '927817364' }, { address: 'afddsfg', id: registrable_user:wbaf52pgvsk2sobpz78l, nif: '214517836', phone: '920123456' }, { address: 'Bla Bla', id: registrable_user:zipcsugqkunrf8vu06cv, nif: '261734892', phone: '923100957' } ];

-- ------------------------------
-- TABLE: repair
-- ------------------------------

DEFINE TABLE repair TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD cost ON repair TYPE null | decimal DEFAULT NULL PERMISSIONS FULL;
DEFINE FIELD duration ON repair TYPE null | duration DEFAULT NULL PERMISSIONS FULL;
DEFINE FIELD end_date ON repair TYPE none | datetime PERMISSIONS FULL;
DEFINE FIELD observations ON repair TYPE none | string PERMISSIONS FULL;
DEFINE FIELD repair_code ON repair TYPE string DEFAULT fn::generate_repair_code() PERMISSIONS FULL;
DEFINE FIELD start_date ON repair TYPE datetime DEFAULT time::now() PERMISSIONS FULL;
DEFINE FIELD state ON repair TYPE string DEFAULT 'PENDING' ASSERT $value INSIDE ['PENDING', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED', 'ARCHIVED', 'REJECTED_BY_ADMIN', 'REJECTED_BY_EMPLOYEE'] PERMISSIONS FULL;

DEFINE INDEX unique_repair_code ON repair FIELDS repair_code UNIQUE;

DEFINE EVENT repair_duration ON repair WHEN $event = 'UPDATE' AND $before.end_date = NONE AND $after.end_date != NONE THEN { UPDATE $after.id SET duration = $after.end_date - $after.start_date };
DEFINE EVENT repair_last_repair_date ON repair WHEN $event = 'UPDATE' AND $before.state != $after.state AND $after.state = 'IN_PROGRESS' THEN { LET $equipment = (SELECT in FROM contains WHERE out = $after.id)[0].in; UPDATE $equipment SET last_repair_date = $after.start_date; };

-- ------------------------------
-- TABLE DATA: repair
-- ------------------------------

INSERT [ { cost: NULL, duration: NULL, id: repair:lb19dtyzlp9qfdgoqltb, repair_code: '120260418205530', start_date: d'2026-04-18T20:55:30.895036219Z', state: 'ARCHIVED' }, { cost: NULL, duration: NULL, id: repair:lt367ml0igxscnoi4egz, observations: 'porque posso', repair_code: '320260628003305', start_date: d'2026-06-28T00:33:05.042259547Z', state: 'ACCEPTED' }, { cost: NULL, duration: NULL, id: repair:vxbtnpig622ojslypzc9, observations: 'nao possui os requesitos', repair_code: '220260627223136', start_date: d'2026-06-27T22:31:36.717085322Z', state: 'ARCHIVED' } ];

-- ------------------------------
-- TABLE: test
-- ------------------------------

DEFINE TABLE test TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD date ON test TYPE datetime PERMISSIONS FULL;
DEFINE FIELD description ON test TYPE string PERMISSIONS FULL;
DEFINE FIELD designation ON test TYPE string PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: test
-- ------------------------------


-- ------------------------------
-- TABLE: used_parts
-- ------------------------------

DEFINE TABLE used_parts TYPE RELATION IN repair OUT part SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON used_parts TYPE record<repair> PERMISSIONS FULL;
DEFINE FIELD out ON used_parts TYPE record<part> PERMISSIONS FULL;
DEFINE FIELD quantity ON used_parts TYPE int PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: used_parts
-- ------------------------------


-- ------------------------------
-- TABLE: user
-- ------------------------------

DEFINE TABLE user TYPE NORMAL SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD email ON user TYPE string ASSERT string::is_email($value) PERMISSIONS FULL;
DEFINE FIELD image ON user TYPE string DEFAULT '' PERMISSIONS FULL;
DEFINE FIELD name ON user TYPE string PERMISSIONS FULL;
DEFINE FIELD password ON user TYPE string PERMISSIONS FULL;
DEFINE FIELD status ON user TYPE string DEFAULT 'PENDING' ASSERT $value INSIDE ['ACTIVE', 'INACTIVE', 'REJECTED', 'PENDING'] PERMISSIONS FULL;
DEFINE FIELD type ON user TYPE string ASSERT $value INSIDE ['CLIENT', 'EMPLOYEE', 'ADMIN'] PERMISSIONS FULL;
DEFINE FIELD username ON user TYPE string PERMISSIONS FULL;

DEFINE INDEX unique_username ON user FIELDS username UNIQUE;

DEFINE EVENT log_user_create ON user WHEN $event = 'CREATE' THEN { CREATE log SET action = 'INSERT', created_at = time::now(), details = string::concat("User '", $after.username, "' (", $after.type, ') was registered in the system.'), user = $after.id };
DEFINE EVENT log_user_update ON user WHEN $event = 'UPDATE' THEN { LET $c_name = (IF $before.name != $after.name { ["name: '" + $before.name ?? '' + "'->'" + $after.name ?? '' + "'"] } ELSE { [] }); LET $c_username = (IF $before.username != $after.username { ["username:'" + $before.username ?? '' + "'->'" + $after.username ?? '' + "'"] } ELSE { [] }); LET $c_email = (IF $before.email != $after.email { ["email: '" + $before.email ?? '' + "'->'" + $after.email ?? '' + "'"] } ELSE { [] }); LET $c_status = (IF $before.status != $after.status { ["status: '" + $before.status ?? '' + "' -> '" + $after.status ?? '' + "'"] } ELSE { [] }); LET $c_password = (IF $before.password != $after.password { ['password changed'] } ELSE { [] }); LET $changes = array::flatten([$c_name, $c_username, $c_email, $c_status, $c_password]); CREATE log SET action = 'UPDATE', created_at = time::now(), details = 'User updated. Changes: ' + array::join($changes, ', '), user = $after.id; };

-- ------------------------------
-- TABLE DATA: user
-- ------------------------------

INSERT [ { email: 'admin@admin.com', id: user:3xn26pscybryt47nhopo, image: '', name: 'Admin', password: 'admin', status: 'ACTIVE', type: 'ADMIN', username: 'admin' }, { email: 'asfsdg@saf.com', id: user:7cn3sw8qstj5n7zjw8lf, image: 'cb9ce9df-e2d4-4299-9bdd-c85b9cb87e47.jpg', name: 'AAAAAAHHHHHHHH', password: 'testclient', status: 'PENDING', type: 'CLIENT', username: 'testclient' }, { email: 'dhjgasfgd@dfsdgsfdfsdfdsfsdf.com', id: user:81b7mgqv0p07m4vo5tda, image: '7ee2d308-57d5-415f-a3c3-05f5a7e8e334.jpg', name: 'Moo Deng', password: 'moodeng', status: 'ACTIVE', type: 'EMPLOYEE', username: 'moodeng' }, { email: 'asdfsa@asd.sad', id: user:au3s6lse81nckxrfaqm5, image: '', name: 'employee teste1', password: 'employee', status: 'ACTIVE', type: 'EMPLOYEE', username: 'employee' }, { email: 'dinismyroshnyk2@protonmail.com', id: user:mjpplilcnwj0wi8p9i5u, image: '', name: 'BlaBla II', password: 'blabla02', status: 'PENDING', type: 'CLIENT', username: 'blabla02' }, { email: 'employeee@gmail.com', id: user:wn7cijtudomcji7c4akx, image: '5da60c2c-54cd-4cd0-adea-969397668ea2.jpg', name: 'Teste employee', password: 'employee', status: 'ACTIVE', type: 'EMPLOYEE', username: 'emp' }, { email: 'asdas@asd.asd', id: user:wx3rb1hmzootjuhnrdur, image: '', name: 'admin2', password: 'admin2', status: 'ACTIVE', type: 'ADMIN', username: 'admin2' }, { email: 'fsdfdsf@asd.aasafqeqeeee', id: user:x8t7ipszhpkfvyuk3x8u, image: '39bc72bd-cf93-48c3-b967-0fe310b39c74.jpg', name: 'Estou cansado', password: 'blabla', status: 'ACTIVE', type: 'CLIENT', username: 'blabla' }, { email: 'client@client.com', id: user:y3ea297c5cwk9d30k7aa, image: '', name: 'Test Client I', password: 'client', status: 'ACTIVE', type: 'CLIENT', username: 'client' } ];

-- ------------------------------
-- TABLE: user_repair
-- ------------------------------

DEFINE TABLE user_repair TYPE RELATION IN user OUT repair SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON user_repair TYPE record<user> PERMISSIONS FULL;
DEFINE FIELD out ON user_repair TYPE record<repair> PERMISSIONS FULL;


DEFINE EVENT log_repair_create ON user_repair WHEN $event = 'CREATE' AND array::len((SELECT * FROM user_repair WHERE out = $after.out)) = 1 THEN { LET $user = (SELECT name FROM user WHERE id = $after.in)[0]; CREATE log SET action = 'INSERT', created_at = time::now(), details = string::concat("Repair request '", type::string($after.out), "' was submitted by '", $user.name, "'."), user = $after.in; };
DEFINE EVENT log_repair_update ON user_repair WHEN $event = 'CREATE' AND array::len((SELECT * FROM user_repair WHERE out = $after.out)) > 1 THEN { LET $repair = (SELECT state FROM repair WHERE id = $after.out)[0]; LET $user = (SELECT name FROM user WHERE id = $after.in)[0]; CREATE log SET action = 'UPDATE', created_at = time::now(), details = string::concat("Repair '", type::string($after.out), "' changed state to '", $repair.state, "' by '", $user.name, "'."), user = $after.in; };

-- ------------------------------
-- TABLE DATA: user_repair
-- ------------------------------

INSERT RELATION [ { id: user_repair:6fz0ytylx9201ff5se7q, in: user:x8t7ipszhpkfvyuk3x8u, out: repair:vxbtnpig622ojslypzc9 }, { id: user_repair:kgqr67lpmsbkqqipmyah, in: user:au3s6lse81nckxrfaqm5, out: repair:lb19dtyzlp9qfdgoqltb }, { id: user_repair:u2v61juvpph7js8psnze, in: user:x8t7ipszhpkfvyuk3x8u, out: repair:lt367ml0igxscnoi4egz }, { id: user_repair:vxgg479dps4hfxa2twxm, in: user:wn7cijtudomcji7c4akx, out: repair:lt367ml0igxscnoi4egz }, { id: user_repair:xehi3z1dh2zswutq1110, in: user:81b7mgqv0p07m4vo5tda, out: repair:lt367ml0igxscnoi4egz }, { id: user_repair:ybjvsp1to6rr1iym8x3r, in: user:x8t7ipszhpkfvyuk3x8u, out: repair:lb19dtyzlp9qfdgoqltb } ];

-- ------------------------------
-- TABLE: viewed_notification
-- ------------------------------

DEFINE TABLE viewed_notification TYPE RELATION IN user OUT notification SCHEMAFULL PERMISSIONS NONE;

DEFINE FIELD in ON viewed_notification TYPE record<user> PERMISSIONS FULL;
DEFINE FIELD out ON viewed_notification TYPE record<notification> PERMISSIONS FULL;



-- ------------------------------
-- TABLE DATA: viewed_notification
-- ------------------------------

INSERT RELATION [ { id: viewed_notification:1awp38e3yms1ixzu9icj, in: user:au3s6lse81nckxrfaqm5, out: notification:qy4az1iu57ojm04goh0s }, { id: viewed_notification:1mkg3xheznfk5wb6rj5x, in: user:3xn26pscybryt47nhopo, out: notification:qtbkmvazc4v2ltunrs4u }, { id: viewed_notification:2jdsyosn3l6k2hqe6uyb, in: user:3xn26pscybryt47nhopo, out: notification:pgmay7d8hgowfz84jf4s }, { id: viewed_notification:3yqml5gagl9es5ssated, in: user:3xn26pscybryt47nhopo, out: notification:qxlen7b1z28wv6hpxk79 }, { id: viewed_notification:6jlsurv5edl6m2x0hx9r, in: user:x8t7ipszhpkfvyuk3x8u, out: notification:ii0hudk6sdu8p4m23mez }, { id: viewed_notification:93zwqf0yu2fcgw5qzijz, in: user:3xn26pscybryt47nhopo, out: notification:g3z0r1bzhxlldd45ujl5 }, { id: viewed_notification:abspfcghnkv4aehguvob, in: user:3xn26pscybryt47nhopo, out: notification:qtbkmvazc4v2ltunrs4u }, { id: viewed_notification:bdoala4n2bf4qsr5z4ao, in: user:3xn26pscybryt47nhopo, out: notification:pgmay7d8hgowfz84jf4s }, { id: viewed_notification:eyfistcmxngax4x8we9g, in: user:3xn26pscybryt47nhopo, out: notification:qtbkmvazc4v2ltunrs4u }, { id: viewed_notification:gp08qi732bxzgdmasa7d, in: user:au3s6lse81nckxrfaqm5, out: notification:r6y2orikks6ft6wwwqjn }, { id: viewed_notification:gvbdq58dumvfm8b6j6a2, in: user:3xn26pscybryt47nhopo, out: notification:1a922xr2dnehgxpf5ixz }, { id: viewed_notification:hntaqu3r9msrfhyhu7z4, in: user:x8t7ipszhpkfvyuk3x8u, out: notification:ii0hudk6sdu8p4m23mez }, { id: viewed_notification:k7axcfhc8h23itxonelh, in: user:3xn26pscybryt47nhopo, out: notification:pgmay7d8hgowfz84jf4s }, { id: viewed_notification:lwkgf32d48dwj9np95u4, in: user:3xn26pscybryt47nhopo, out: notification:p6indc5kfodbkfs9iba5 }, { id: viewed_notification:nyusnhekj4mq7oroz9o0, in: user:x8t7ipszhpkfvyuk3x8u, out: notification:hnoxlie77ew4gprorn0t }, { id: viewed_notification:q12reh1d35sy2nn7gkkl, in: user:3xn26pscybryt47nhopo, out: notification:p6indc5kfodbkfs9iba5 }, { id: viewed_notification:q1bg2rqjnhb4u0d4v9en, in: user:3xn26pscybryt47nhopo, out: notification:pgmay7d8hgowfz84jf4s }, { id: viewed_notification:q20x8xywrktlwchwslwy, in: user:x8t7ipszhpkfvyuk3x8u, out: notification:7kfulb4tdbrgifvqa6o4 }, { id: viewed_notification:skyi9otbv2m9t8fpw2de, in: user:3xn26pscybryt47nhopo, out: notification:pgmay7d8hgowfz84jf4s }, { id: viewed_notification:t216b4sbcsqqawtjlwik, in: user:3xn26pscybryt47nhopo, out: notification:pgmay7d8hgowfz84jf4s }, { id: viewed_notification:vw0ex3emiiydyf9qh3ya, in: user:3xn26pscybryt47nhopo, out: notification:pgmay7d8hgowfz84jf4s }, { id: viewed_notification:w80zhjtztlvhd9po03g2, in: user:wn7cijtudomcji7c4akx, out: notification:tyhor5gidaomghrk8epo }, { id: viewed_notification:wkfwkx65klotx3jj273a, in: user:3xn26pscybryt47nhopo, out: notification:p6indc5kfodbkfs9iba5 }, { id: viewed_notification:wr7ztkxj54bkpfcwi3rn, in: user:3xn26pscybryt47nhopo, out: notification:pgmay7d8hgowfz84jf4s }, { id: viewed_notification:xnazunf78po3exnubzvm, in: user:3xn26pscybryt47nhopo, out: notification:d9tqpgiaangbo8m8hr4h }, { id: viewed_notification:y55eqecf67lq838r2ypa, in: user:3xn26pscybryt47nhopo, out: notification:yyhlp9hv47i7yhe0v6dm } ];

