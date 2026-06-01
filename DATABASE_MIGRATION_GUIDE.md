# Database Migration Guide

MentorKE does not currently use Flyway or Liquibase. Schema changes are applied manually with SQL.

## Current migration

Run [src/main/resources/db/migration/V1__add_analytics_and_rating_columns.sql](src/main/resources/db/migration/V1__add_analytics_and_rating_columns.sql) against the PostgreSQL database used by the app.

## Using psql

```bash
psql -h localhost -U postgres -d mentorke -f src/main/resources/db/migration/V1__add_analytics_and_rating_columns.sql
```

If your database name, host, or username is different, replace them with your actual values.

## What this migration adds

- `mentors.location`
- `mentors.availability`
- `mentors.average_rating`
- `mentors.rating_count`
- `sessions.mentor_rating`
- `sessions.rating_feedback`
- `sessions.rating_requested_at`
- `sessions.rated_at`

## After applying the migration

Restart WildFly and redeploy the application.
