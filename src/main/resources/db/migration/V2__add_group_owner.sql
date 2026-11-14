 -- 1. Añadir la columna (todavía admite NULL)
  ALTER TABLE groups
      ADD COLUMN group_owner_id BIGINT REFERENCES users (id);

  -- 2. Rellenarla en los grupos existentes con su ADMIN más antiguo
  UPDATE groups g
  SET group_owner_id = (
      SELECT gm.user_id
      FROM group_members gm
      WHERE gm.group_id = g.id
        AND gm.user_role = 'ADMIN'
      ORDER BY gm.joined_at
      LIMIT 1
  );

  -- 3. Ahora que todos tienen valor, hacerla obligatoria
  ALTER TABLE groups
      ALTER COLUMN group_owner_id SET NOT NULL;
