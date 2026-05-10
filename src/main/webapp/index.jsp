<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MentorKE — Grow With Guidance</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --blue-900: #0a2e6e;
            --blue-800: #0d47a1;
            --blue-700: #1565c0;
            --blue-600: #1976d2;
            --blue-100: #bbdefb;
            --blue-50:  #e3f2fd;
            --blue-25:  #f0f7ff;
            --white:    #ffffff;
            --gray-50:  #f8fafc;
            --gray-100: #f1f5f9;
            --gray-200: #e2e8f0;
            --gray-400: #94a3b8;
            --gray-600: #475569;
            --gray-800: #1e293b;
        }

        html { scroll-behavior: smooth; }

        body {
            font-family: 'DM Sans', sans-serif;
            background: var(--white);
            color: var(--gray-800);
        }

        /* ── NAVBAR ── */
        .navbar {
            position: sticky; top: 0; z-index: 100;
            background: var(--white);
            border-bottom: 1px solid var(--gray-200);
            padding: 0 60px;
            height: 64px;
            display: flex; align-items: center; justify-content: space-between;
        }
        .nav-brand {
            display: flex; align-items: center; gap: 10px;
            text-decoration: none;
        }
        .nav-brand-icon {
            width: 34px; height: 34px;
            background: var(--blue-800);
            border-radius: 8px;
            display: flex; align-items: center; justify-content: center;
        }
        .nav-brand-icon svg { width: 18px; height: 18px; }
        .nav-brand-name {
            font-size: 18px; font-weight: 700;
            color: var(--blue-800); letter-spacing: -0.3px;
        }
        .nav-links {
            display: flex; align-items: center; gap: 6px;
        }
        .nav-links a {
            padding: 7px 14px;
            border-radius: 7px;
            font-size: 14px; font-weight: 500;
            color: var(--gray-600);
            text-decoration: none;
            transition: background 0.15s, color 0.15s;
        }
        .nav-links a:hover { background: var(--gray-100); color: var(--gray-800); }
        .nav-links .btn-nav-outline {
            border: 1px solid var(--gray-200);
            color: var(--gray-800);
        }
        .nav-links .btn-nav-primary {
            background: var(--blue-800); color: var(--white);
            padding: 7px 18px;
        }
        .nav-links .btn-nav-primary:hover { background: var(--blue-700); color: var(--white); }

        /* ── HERO ── */
        .hero {
            background: linear-gradient(160deg, var(--blue-900) 0%, var(--blue-800) 55%, var(--blue-700) 100%);
            padding: 100px 60px 90px;
            text-align: center;
            position: relative;
            overflow: hidden;
        }
        .hero::before {
            content: '';
            position: absolute; inset: 0;
            background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
        }
        .hero-tag {
            display: inline-flex; align-items: center; gap: 7px;
            background: rgba(255,255,255,0.12);
            border: 1px solid rgba(255,255,255,0.2);
            border-radius: 20px;
            padding: 5px 14px 5px 10px;
            font-size: 13px; font-weight: 500; color: rgba(255,255,255,0.9);
            margin-bottom: 28px;
        }
        .hero-tag-dot {
            width: 7px; height: 7px; border-radius: 50%;
            background: #4ade80;
            animation: pulse 2s infinite;
        }
        @keyframes pulse {
            0%,100% { opacity: 1; }
            50% { opacity: 0.4; }
        }
        .hero h1 {
            font-size: 52px; font-weight: 700;
            color: var(--white);
            line-height: 1.12;
            letter-spacing: -1px;
            max-width: 720px;
            margin: 0 auto 20px;
        }
        .hero h1 span { color: var(--blue-100); }
        .hero p {
            font-size: 18px; color: rgba(255,255,255,0.75);
            max-width: 540px; margin: 0 auto 40px;
            line-height: 1.7;
        }
        .hero-cta {
            display: flex; align-items: center; justify-content: center;
            gap: 12px; flex-wrap: wrap;
        }
        .btn-hero-primary {
            display: inline-flex; align-items: center; gap: 8px;
            background: var(--white); color: var(--blue-800);
            padding: 13px 26px;
            border-radius: 9px;
            font-size: 15px; font-weight: 600;
            text-decoration: none;
            transition: transform 0.15s, box-shadow 0.15s;
            box-shadow: 0 4px 14px rgba(0,0,0,0.15);
        }
        .btn-hero-primary:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(0,0,0,0.2); }
        .btn-hero-outline {
            display: inline-flex; align-items: center; gap: 8px;
            background: rgba(255,255,255,0.1);
            border: 1px solid rgba(255,255,255,0.25);
            color: var(--white);
            padding: 13px 26px;
            border-radius: 9px;
            font-size: 15px; font-weight: 500;
            text-decoration: none;
            transition: background 0.15s;
        }
        .btn-hero-outline:hover { background: rgba(255,255,255,0.18); }

        .hero-stats {
            display: flex; align-items: center; justify-content: center;
            gap: 40px; margin-top: 60px;
            flex-wrap: wrap;
        }
        .hero-stat { text-align: center; }
        .hero-stat-num {
            font-size: 28px; font-weight: 700; color: var(--white);
            letter-spacing: -0.5px;
        }
        .hero-stat-label { font-size: 13px; color: rgba(255,255,255,0.55); margin-top: 3px; }
        .hero-stat-divider {
            width: 1px; height: 40px;
            background: rgba(255,255,255,0.15);
        }

        /* ── SECTION COMMON ── */
        section { padding: 80px 60px; }
        .section-label {
            display: inline-block;
            font-size: 12px; font-weight: 600;
            letter-spacing: 0.08em; text-transform: uppercase;
            color: var(--blue-800);
            background: var(--blue-50);
            border: 1px solid var(--blue-100);
            padding: 4px 12px; border-radius: 20px;
            margin-bottom: 14px;
        }
        .section-title {
            font-size: 34px; font-weight: 700;
            color: var(--gray-800); letter-spacing: -0.5px;
            line-height: 1.2; margin-bottom: 14px;
        }
        .section-sub {
            font-size: 16px; color: var(--gray-400);
            line-height: 1.7; max-width: 500px;
        }

        /* ── HOW IT WORKS ── */
        .how-section { background: var(--gray-50); }
        .how-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 30px;
            margin-top: 50px;
        }
        .how-card {
            background: var(--white);
            border: 1px solid var(--gray-200);
            border-radius: 14px;
            padding: 28px;
            position: relative;
        }
        .how-number {
            width: 36px; height: 36px;
            background: var(--blue-800); color: var(--white);
            border-radius: 9px;
            display: flex; align-items: center; justify-content: center;
            font-size: 15px; font-weight: 700;
            margin-bottom: 18px;
        }
        .how-card h3 {
            font-size: 17px; font-weight: 600;
            color: var(--gray-800); margin-bottom: 10px;
        }
        .how-card p { font-size: 14px; color: var(--gray-400); line-height: 1.7; }
        .how-card-arrow {
            position: absolute; right: -16px; top: 50%;
            transform: translateY(-50%);
            width: 32px; height: 32px;
            background: var(--blue-50); border: 1px solid var(--blue-100);
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            z-index: 1;
        }
        .how-card-arrow svg { width: 14px; height: 14px; color: var(--blue-800); }
        .how-card:last-child .how-card-arrow { display: none; }

        /* ── FEATURES ── */
        .features-section { background: var(--white); }
        .features-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
            margin-top: 50px;
        }
        .feature-card {
            border: 1px solid var(--gray-200);
            border-radius: 14px;
            padding: 26px;
            transition: box-shadow 0.2s, transform 0.2s;
        }
        .feature-card:hover {
            box-shadow: 0 8px 30px rgba(13,71,161,0.08);
            transform: translateY(-2px);
        }
        .feature-icon {
            width: 44px; height: 44px;
            border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            margin-bottom: 16px;
        }
        .feature-icon svg { width: 22px; height: 22px; }
        .fi-blue   { background: var(--blue-50); }
        .fi-teal   { background: #e0f2f1; }
        .fi-green  { background: #f0fdf4; }
        .fi-purple { background: #f3e8ff; }
        .fi-amber  { background: #fffbeb; }
        .fi-red    { background: #fef2f2; }
        .feature-card h3 {
            font-size: 16px; font-weight: 600;
            color: var(--gray-800); margin-bottom: 8px;
        }
        .feature-card p { font-size: 14px; color: var(--gray-400); line-height: 1.7; }

        /* ── TESTIMONIALS ── */
        .testimonials-section { background: var(--blue-800); }
        .testimonials-section .section-label {
            background: rgba(255,255,255,0.12);
            border-color: rgba(255,255,255,0.2);
            color: rgba(255,255,255,0.8);
        }
        .testimonials-section .section-title { color: var(--white); }
        .testimonials-section .section-sub   { color: rgba(255,255,255,0.6); }
        .testimonials-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
            margin-top: 50px;
        }
        .testimonial-card {
            background: rgba(255,255,255,0.08);
            border: 1px solid rgba(255,255,255,0.12);
            border-radius: 14px;
            padding: 24px;
        }
        .testimonial-quote {
            font-size: 14px; color: rgba(255,255,255,0.8);
            line-height: 1.8; margin-bottom: 20px;
        }
        .testimonial-quote::before { content: '\201C'; font-size: 22px; color: var(--blue-100); }
        .testimonial-author {
            display: flex; align-items: center; gap: 10px;
        }
        .testimonial-avatar {
            width: 36px; height: 36px; border-radius: 50%;
            background: rgba(255,255,255,0.2);
            display: flex; align-items: center; justify-content: center;
            font-size: 13px; font-weight: 600; color: var(--white);
            flex-shrink: 0;
        }
        .testimonial-name  { font-size: 13px; font-weight: 600; color: var(--white); }
        .testimonial-role  { font-size: 12px; color: rgba(255,255,255,0.5); }

        /* ── CTA BANNER ── */
        .cta-section {
            background: var(--blue-25);
            border-top: 1px solid var(--blue-100);
            border-bottom: 1px solid var(--blue-100);
            text-align: center;
            padding: 80px 60px;
        }
        .cta-section .section-title { max-width: 560px; margin: 0 auto 14px; }
        .cta-section .section-sub   { margin: 0 auto 36px; }
        .cta-btns { display: flex; gap: 12px; justify-content: center; flex-wrap: wrap; }
        .btn-cta-primary {
            display: inline-flex; align-items: center; gap: 8px;
            background: var(--blue-800); color: var(--white);
            padding: 13px 28px; border-radius: 9px;
            font-size: 15px; font-weight: 600; text-decoration: none;
            transition: background 0.15s, transform 0.15s;
        }
        .btn-cta-primary:hover { background: var(--blue-700); transform: translateY(-1px); }
        .btn-cta-outline {
            display: inline-flex; align-items: center; gap: 8px;
            background: var(--white);
            border: 1px solid var(--gray-200);
            color: var(--gray-800);
            padding: 13px 28px; border-radius: 9px;
            font-size: 15px; font-weight: 500; text-decoration: none;
            transition: background 0.15s;
        }
        .btn-cta-outline:hover { background: var(--gray-100); }

        /* ── FOOTER ── */
        .footer {
            background: var(--blue-900);
            padding: 50px 60px 30px;
        }
        .footer-top {
            display: grid;
            grid-template-columns: 1.5fr 1fr 1fr;
            gap: 40px;
            padding-bottom: 40px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        .footer-brand-name {
            font-size: 18px; font-weight: 700; color: var(--white); margin-bottom: 10px;
        }
        .footer-brand-desc {
            font-size: 13px; color: rgba(255,255,255,0.45); line-height: 1.7; max-width: 260px;
        }
        .footer-col-title {
            font-size: 12px; font-weight: 600;
            color: rgba(255,255,255,0.4);
            text-transform: uppercase; letter-spacing: 0.08em;
            margin-bottom: 16px;
        }
        .footer-col a {
            display: block; font-size: 14px;
            color: rgba(255,255,255,0.6);
            text-decoration: none; margin-bottom: 10px;
            transition: color 0.15s;
        }
        .footer-col a:hover { color: var(--white); }
        .footer-bottom {
            padding-top: 24px;
            display: flex; align-items: center; justify-content: space-between;
            font-size: 13px; color: rgba(255,255,255,0.35);
        }
    </style>
</head>
<body>

    <!-- ══════════ NAVBAR ══════════ -->
    <nav class="navbar">
        <a href="index" class="nav-brand">
            <div class="nav-brand-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </div>
            <span class="nav-brand-name">MentorKE</span>
        </a>
        <div class="nav-links">
            <a href="index">Home</a>
            <a href="about">About</a>
            <a href="login" class="btn-nav-outline">Login</a>
            <a href="register" class="btn-nav-primary">Get started</a>
        </div>
    </nav>

    <!-- ══════════ HERO ══════════ -->
    <section class="hero">
        <div class="hero-tag">
            <span class="hero-tag-dot"></span>
            Kenya's mentorship platform
        </div>
        <h1>Connect, grow and <span>thrive</span> with the right mentor</h1>
        <p>MentorKE pairs ambitious Kenyans with experienced professionals for personalised, goal-driven mentorship journeys.</p>
        <div class="hero-cta">
            <a href="register" class="btn-hero-primary">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                Get started — it's free
            </a>
            <a href="about" class="btn-hero-outline">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polygon points="10 8 16 12 10 16 10 8"/></svg>
                Learn more
            </a>
        </div>
        <div class="hero-stats">
            <div class="hero-stat">
                <div class="hero-stat-num">500+</div>
                <div class="hero-stat-label">Active mentors</div>
            </div>
            <div class="hero-stat-divider"></div>
            <div class="hero-stat">
                <div class="hero-stat-num">2,000+</div>
                <div class="hero-stat-label">Mentees enrolled</div>
            </div>
            <div class="hero-stat-divider"></div>
            <div class="hero-stat">
                <div class="hero-stat-num">47</div>
                <div class="hero-stat-label">Counties reached</div>
            </div>
            <div class="hero-stat-divider"></div>
            <div class="hero-stat">
                <div class="hero-stat-num">95%</div>
                <div class="hero-stat-label">Satisfaction rate</div>
            </div>
        </div>
    </section>

    <!-- ══════════ HOW IT WORKS ══════════ -->
    <section class="how-section">
        <div style="max-width:1100px; margin:0 auto;">
            <span class="section-label">How it works</span>
            <h2 class="section-title">Three steps to your mentorship</h2>
            <p class="section-sub">Getting started with MentorKE is simple and takes less than five minutes.</p>

            <div class="how-grid">
                <div class="how-card">
                    <div class="how-number">1</div>
                    <h3>Create your profile</h3>
                    <p>Register as a mentee or mentor. Tell us about your background, field of study, goals, and what you're looking for.</p>
                    <div class="how-card-arrow">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
                    </div>
                </div>
                <div class="how-card">
                    <div class="how-number">2</div>
                    <h3>Get matched</h3>
                    <p>Our admins review your profile and pair you with a mentor whose expertise aligns with your learning goals and career path.</p>
                    <div class="how-card-arrow">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
                    </div>
                </div>
                <div class="how-card">
                    <div class="how-number">3</div>
                    <h3>Start growing</h3>
                    <p>Connect with your mentor, set milestones, track your progress, and build the skills and network you need to succeed.</p>
                    <div class="how-card-arrow"></div>
                </div>
            </div>
        </div>
    </section>

    <!-- ══════════ FEATURES ══════════ -->
    <section class="features-section">
        <div style="max-width:1100px; margin:0 auto;">
            <span class="section-label">Platform features</span>
            <h2 class="section-title">Everything you need to grow</h2>
            <p class="section-sub">MentorKE gives you the tools to take your career and learning journey to the next level.</p>

            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon fi-blue">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#1565c0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                    </div>
                    <h3>Expert mentors</h3>
                    <p>Access a diverse pool of vetted professionals from tech, business, healthcare, and more across Kenya.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon fi-teal">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#0f6e56" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
                    </div>
                    <h3>Progress tracking</h3>
                    <p>Set learning goals and monitor your progress with a personalised dashboard that keeps you on track.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon fi-green">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#15803d" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                    </div>
                    <h3>Session management</h3>
                    <p>Schedule, manage and keep track of your mentorship sessions easily from your dashboard.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon fi-purple">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#7c3aed" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
                    </div>
                    <h3>Field-specific matching</h3>
                    <p>Get paired with a mentor based on your actual field of study and career aspirations, not just availability.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon fi-amber">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#b45309" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                    </div>
                    <h3>Personalised goals</h3>
                    <p>Define what success looks like for you. Your mentor works with your goals, not a generic curriculum.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon fi-red">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#b91c1c" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                    </div>
                    <h3>Secure & trusted</h3>
                    <p>All mentors are reviewed and verified. Your data and interactions are handled with full confidentiality.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- ══════════ TESTIMONIALS ══════════ -->
    <section class="testimonials-section">
        <div style="max-width:1100px; margin:0 auto;">
            <span class="section-label">Testimonials</span>
            <h2 class="section-title">What our community says</h2>
            <p class="section-sub">Real stories from mentees and mentors who have experienced the MentorKE difference.</p>

            <div class="testimonials-grid">
                <div class="testimonial-card">
                    <div class="testimonial-quote">
                        MentorKE connected me with a senior engineer who completely transformed how I approach problem solving. Within three months I had my first internship offer.
                    </div>
                    <div class="testimonial-author">
                        <div class="testimonial-avatar">AW</div>
                        <div>
                            <div class="testimonial-name">Alice Wanjiru</div>
                            <div class="testimonial-role">Computer Science student, Nairobi</div>
                        </div>
                    </div>
                </div>
                <div class="testimonial-card">
                    <div class="testimonial-quote">
                        As a mentor, this platform gives me a structured way to give back. The matching is thoughtful and the mentees are genuinely motivated to grow.
                    </div>
                    <div class="testimonial-author">
                        <div class="testimonial-avatar">JM</div>
                        <div>
                            <div class="testimonial-name">Dr. James Mwangi</div>
                            <div class="testimonial-role">Senior Software Engineer, Mentor</div>
                        </div>
                    </div>
                </div>
                <div class="testimonial-card">
                    <div class="testimonial-quote">
                        I was lost about my career in data science. My mentor helped me build a roadmap, recommended resources, and reviewed my projects. It was invaluable.
                    </div>
                    <div class="testimonial-author">
                        <div class="testimonial-avatar">GK</div>
                        <div>
                            <div class="testimonial-name">Grace Kipchoge</div>
                            <div class="testimonial-role">Data Science mentee, Eldoret</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- ══════════ CTA ══════════ -->
    <section class="cta-section">
        <div style="max-width:1100px; margin:0 auto;">
            <span class="section-label">Ready to begin?</span>
            <h2 class="section-title">Start your mentorship journey today</h2>
            <p class="section-sub">Join thousands of Kenyans already growing with MentorKE. Registration is free and takes under five minutes.</p>
            <div class="cta-btns">
                <a href="register" class="btn-cta-primary">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                    Create a free account
                </a>
                <a href="login" class="btn-cta-outline">
                    Already have an account? Login
                </a>
            </div>
        </div>
    </section>

    <!-- ══════════ FOOTER ══════════ -->
    <footer class="footer">
        <div style="max-width:1100px; margin:0 auto;">
            <div class="footer-top">
                <div>
                    <div class="footer-brand-name">MentorKE</div>
                    <p class="footer-brand-desc">Connecting mentors and mentees across Kenya for growth, guidance, and lasting opportunity.</p>
                </div>
                <div class="footer-col">
                    <div class="footer-col-title">Platform</div>
                    <a href="index">Home</a>
                    <a href="about">About</a>
                    <a href="register">Register</a>
                    <a href="login">Login</a>
                </div>
                <div class="footer-col">
                    <div class="footer-col-title">Roles</div>
                    <a href="register">Join as a mentee</a>
                    <a href="register">Join as a mentor</a>
                    <a href="login">Admin login</a>
                </div>
            </div>
            <div class="footer-bottom">
                <span>&copy; 2026 MentorKE. All rights reserved. Built in Kenya.</span>
                <span>Nairobi, Kenya</span>
            </div>
        </div>
    </footer>

</body>
</html>