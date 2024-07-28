USE atn;
SELECT p.*, pg.* FROM  POINTS p JOIN POSTINGS pg ON p.posting_id = pg.id