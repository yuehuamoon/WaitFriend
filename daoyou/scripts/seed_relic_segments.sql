-- 1. 开启事务
START TRANSACTION;

-- 2. 调大拼接限制
SET SESSION group_concat_max_len = 10240000;

-- 3. 插入 100 段内容（使用 CROSS JOIN 生成 1~100，避开客户端对 WITH 的解析 Bug）
INSERT INTO relic_segments (article_id, generation, seq, pool_round, user_uuid, content)
SELECT 1, 0, seqs.n, seqs.n,
       'ccaf8032-569a-4186-a1e8-d0838aef0c62',
       CONCAT(
               '第', seqs.n, '段。',
               CASE seqs.n % 10
                   WHEN 0 THEN '夜雨落在旧城的青石板上，灯影被风揉碎，行人从檐下匆匆走过。我站在桥边，听水声一层层推向远处，忽然想起那些被岁月掩埋的名字。有人说江湖最怕的不是刀剑，而是无人记得你曾来过。于是我把未说完的话刻进石纹，让后来者在沉默里辨认。'
                   WHEN 1 THEN '清晨的雾从山脚漫上来，把寺门、松枝和石阶都收进一片苍白。守碑的老人递来一盏冷茶，说每一道裂纹都藏着一次选择。我沿着碑林缓步而行，指尖触到粗糙的刻痕，像触到陌生人留下的脉搏。那些看似断续的句子，在风里慢慢连成一条通往过去的路。'
                   WHEN 2 THEN '午后的日光穿过破窗，落在积尘的案几上。我翻开一本没有封面的旧册，纸页间夹着干枯的花与褪色的车票。故事没有开头，也没有写完的结尾，只剩一些认真生活过的证据。读到最后一页时，窗外传来叫卖声，仿佛另一个时代仍在街巷深处继续呼吸。'
                   WHEN 3 THEN '黄昏把城墙染成暗红，远处的钟声一次次越过屋脊。我数着归鸟，也数着心里未解的疑问。有人劝我放下旧事，说前路比回忆重要；可没有来处的人，又如何辨认归途。碑上的字被风雨磨去棱角，却仍守着方向，让每一个迷路的人知道自己并非凭空而生。'
                   WHEN 4 THEN '河面结了一层薄冰，船夫用长篙轻轻点开，碎响像细小的告别。我坐在渡口，看对岸的灯一盏盏亮起，想起曾经同行的人已经在不同方向走远。缘分并不总是轰轰烈烈，有时只是一次回眸、一句保重，然后在漫长岁月里各自安稳。'
                   WHEN 5 THEN '风穿过竹林，叶片彼此摩挲，声音像无数人在低声交谈。我把手贴在碑石上，感到白日余温正一点点退去。世上的答案往往并不锋利，它们藏在一次次迟疑里，藏在想说又止住的沉默里。真正的勇气不是从不害怕，而是明知会失去，仍愿意把心意说清。'
                   WHEN 6 THEN '集市散去后，地上留下菜叶、马蹄印和几片被踩皱的红纸。我沿着摊位的空隙走，闻见酒香与木炭味混在一起。一个孩子追着滚远的铜钱跑过街角，笑声短促而明亮。人间并不需要宏大的解释，一碗热汤、一盏归灯，便足以让疲惫的灵魂重新站稳。'
                   WHEN 7 THEN '雪落得很轻，落在碑顶，也落在我的肩上。四周没有脚印，天地像一张尚未落笔的纸。我想起那些来不及道谢的人，他们曾在雨夜借伞，在低谷递来一句话，在众人散去后仍站在身旁。恩情不必时时提起，但应当被记住，并在下一次相遇时继续传递。'
                   WHEN 8 THEN '旧宅的门轴发出悠长声响，院里那棵树已经高过屋脊。石桌上刻着幼时刻下的名字，笔画歪斜，却比任何誓言都真实。时间带走许多人，也留下一些不会腐烂的东西：对故土的牵挂，对承诺的敬畏，以及愿意为某个人走很远路的笨拙。'
                   ELSE '月升到中天时，我仍在碑前。远处传来更鼓，近处只有虫鸣与水声。人生像一条没有标注终点的长路，走过繁华，也走过荒凉。若有一天后来者读到这里，请不必替我惋惜，只要记住：我曾认真地爱过、怀疑过、选择过，并在最暗的夜里替自己留下一点光。'
                   END,
               '我愿把这一段交给时间，也交给偶然停在碑前的你。若字句之间仍有空隙，那便是留给后人的门。'
       )
FROM (
         -- 用 10x10 的笛卡尔积生成 1 到 100 的数字
         SELECT (a.n + b.n * 10) + 1 AS n
         FROM
             (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a
                 CROSS JOIN
             (SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
     ) seqs
WHERE NOT EXISTS (
    SELECT 1 FROM relic_segments s
    WHERE s.article_id = 1 AND s.generation = 0 AND s.seq = seqs.n
);

-- 4. 更新残碑主表，拼接正文（这一步你的日志里其实成功了，但再跑一遍也没事）
UPDATE articles
SET body = CONCAT(
        COALESCE(relic_root_content, body),
        COALESCE(
                (SELECT GROUP_CONCAT(content ORDER BY seq SEPARATOR '')
                 FROM relic_segments
                 WHERE article_id = 1 AND generation = 0),
                ''
        )
           ),
    relic_pool_round = 100,
    update_time = CURRENT_TIMESTAMP
WHERE id = 1 AND type = 'relic';

-- 5. 强制提交事务
COMMIT;

-- 6. 验证到底写没写进去（看结果是不是 100）
SELECT COUNT(*) AS total_segments FROM relic_segments WHERE article_id = 1;
SELECT LENGTH(body) AS body_length FROM articles WHERE id = 1;