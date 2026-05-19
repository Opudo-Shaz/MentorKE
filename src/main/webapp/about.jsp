<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About Us — MentorKE</title>
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
            --green-50: #f0fdf4;
            --green-700:#15803d;
            --amber-50: #fffbeb;
            --amber-700:#b45309;
        }

        html { scroll-behavior: smooth; }
        body { font-family: 'DM Sans', sans-serif; background: var(--white); color: var(--gray-800); }

        /* ── NAVBAR ── */
        .navbar {
            position: sticky; top: 0; z-index: 100;
            background: var(--white);
            border-bottom: 1px solid var(--gray-200);
            padding: 0 60px; height: 64px;
            display: flex; align-items: center; justify-content: space-between;
        }
        .nav-brand { display: flex; align-items: center; gap: 10px; text-decoration: none; }
        .nav-brand-icon {
            width: 34px; height: 34px; background: var(--blue-800);
            border-radius: 8px; display: flex; align-items: center; justify-content: center;
        }
        .nav-brand-icon svg { width: 18px; height: 18px; }
        .nav-brand-name { font-size: 18px; font-weight: 700; color: var(--blue-800); letter-spacing: -0.3px; }
        .nav-links { display: flex; align-items: center; gap: 6px; }
        .nav-links a {
            padding: 7px 14px; border-radius: 7px;
            font-size: 14px; font-weight: 500; color: var(--gray-600);
            text-decoration: none; transition: background 0.15s, color 0.15s;
        }
        .nav-links a:hover { background: var(--gray-100); color: var(--gray-800); }
        .nav-links a.active { color: var(--blue-800); font-weight: 600; }
        .nav-links .btn-outline { border: 1px solid var(--gray-200); color: var(--gray-800); }
        .nav-links .btn-primary { background: var(--blue-800); color: var(--white); padding: 7px 18px; }
        .nav-links .btn-primary:hover { background: var(--blue-700); color: var(--white); }

        /* ── HERO ── */
        .hero {
            background: linear-gradient(160deg, var(--blue-900) 0%, var(--blue-800) 55%, var(--blue-700) 100%);
            padding: 80px 60px 70px; text-align: center; position: relative; overflow: hidden;
        }
        .hero::before {
            content: '';
            position: absolute; inset: 0;
            background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
        }
        .hero-tag {
            display: inline-flex; align-items: center; gap: 7px;
            background: rgba(255,255,255,0.12); border: 1px solid rgba(255,255,255,0.2);
            border-radius: 20px; padding: 5px 14px 5px 10px;
            font-size: 13px; font-weight: 500; color: rgba(255,255,255,0.9);
            margin-bottom: 24px;
        }
        .hero-tag-dot { width: 7px; height: 7px; border-radius: 50%; background: #4ade80; }
        .hero h1 {
            font-size: 44px; font-weight: 700; color: var(--white);
            line-height: 1.15; letter-spacing: -0.8px;
            max-width: 600px; margin: 0 auto 16px;
        }
        .hero h1 span { color: var(--blue-100); }
        .hero p {
            font-size: 17px; color: rgba(255,255,255,0.72);
            max-width: 480px; margin: 0 auto; line-height: 1.7;
        }

        /* ── SECTION COMMON ── */
        section { padding: 80px 60px; }
        .inner { max-width: 1100px; margin: 0 auto; }
        .section-label {
            display: inline-block; font-size: 12px; font-weight: 600;
            letter-spacing: 0.08em; text-transform: uppercase;
            color: var(--blue-800); background: var(--blue-50);
            border: 1px solid var(--blue-100);
            padding: 4px 12px; border-radius: 20px; margin-bottom: 14px;
        }
        .section-title {
            font-size: 32px; font-weight: 700; color: var(--gray-800);
            letter-spacing: -0.5px; line-height: 1.2; margin-bottom: 14px;
        }
        .section-sub {
            font-size: 16px; color: var(--gray-400); line-height: 1.7; max-width: 520px;
        }

        /* ── STORY ── */
        .story-section { background: var(--gray-50); }
        .story-grid {
            display: grid; grid-template-columns: 1fr 1fr;
            gap: 60px; align-items: center; margin-top: 50px;
        }
        .story-text p {
            font-size: 15px; color: var(--gray-600); line-height: 1.8;
            margin-bottom: 16px;
        }
        .story-text p:last-child { margin-bottom: 0; }
        .story-visual {
            display: grid; grid-template-columns: 1fr 1fr; gap: 14px;
        }
        .story-tile {
            background: var(--white); border: 1px solid var(--gray-200);
            border-radius: 14px; padding: 20px; text-align: center;
            box-shadow: var(--shadow-sm);
        }
        .story-tile-num {
            font-size: 30px; font-weight: 700; color: var(--blue-800);
            letter-spacing: -0.5px; line-height: 1;
        }
        .story-tile-label { font-size: 12px; color: var(--gray-400); margin-top: 5px; }
        .story-tile.highlight {
            background: var(--blue-800); border-color: var(--blue-800);
        }
        .story-tile.highlight .story-tile-num { color: var(--white); }
        .story-tile.highlight .story-tile-label { color: rgba(255,255,255,0.6); }

        /* ── MISSION / VALUES ── */
        .values-section { background: var(--white); }
        .values-grid {
            display: grid; grid-template-columns: repeat(3, 1fr);
            gap: 20px; margin-top: 50px;
        }
        .value-card {
            border: 1px solid var(--gray-200); border-radius: 14px; padding: 26px;
            transition: box-shadow 0.2s, transform 0.2s;
        }
        .value-card:hover { box-shadow: 0 8px 30px rgba(13,71,161,0.08); transform: translateY(-2px); }
        .value-icon {
            width: 44px; height: 44px; border-radius: 10px;
            display: flex; align-items: center; justify-content: center;
            margin-bottom: 16px;
        }
        .value-icon svg { width: 22px; height: 22px; }
        .vi-blue   { background: var(--blue-50); }
        .vi-teal   { background: #e0f2f1; }
        .vi-green  { background: var(--green-50); }
        .vi-amber  { background: var(--amber-50); }
        .vi-purple { background: #f3e8ff; }
        .vi-red    { background: #fef2f2; }
        .value-card h3 { font-size: 16px; font-weight: 600; color: var(--gray-800); margin-bottom: 8px; }
        .value-card p  { font-size: 14px; color: var(--gray-400); line-height: 1.7; }

        /* ── TEAM ── */
        .team-section { background: var(--gray-50); }
        .team-grid {
            display: grid; grid-template-columns: repeat(3, 1fr);
            gap: 20px; margin-top: 50px;
        }
        .team-card {
            background: var(--white); border: 1px solid var(--gray-200);
            border-radius: 14px; padding: 26px; text-align: center;
        }
        .team-avatar {
            width: 60px; height: 60px; border-radius: 50%;
            background: var(--blue-800);
            display: flex; align-items: center; justify-content: center;
            font-size: 20px; font-weight: 700; color: var(--white);
            margin: 0 auto 14px;
        }
        .team-name  { font-size: 15px; font-weight: 600; color: var(--gray-800); }
        .team-role  { font-size: 13px; color: var(--gray-400); margin-top: 3px; margin-bottom: 12px; }
        .team-bio   { font-size: 13px; color: var(--gray-600); line-height: 1.7; }

        /* ── IMPACT (dark section) ── */
        .impact-section { background: var(--blue-800); }
        .impact-section .section-label {
            background: rgba(255,255,255,0.12);
            border-color: rgba(255,255,255,0.2);
            color: rgba(255,255,255,0.8);
        }
        .impact-section .section-title { color: var(--white); }
        .impact-section .section-sub   { color: rgba(255,255,255,0.6); }
        .impact-grid {
            display: grid; grid-template-columns: repeat(4, 1fr);
            gap: 20px; margin-top: 50px;
        }
        .impact-card {
            background: rgba(255,255,255,0.08);
            border: 1px solid rgba(255,255,255,0.12);
            border-radius: 14px; padding: 24px; text-align: center;
        }
        .impact-num {
            font-size: 36px; font-weight: 700; color: var(--white);
            letter-spacing: -1px; line-height: 1;
        }
        .impact-label { font-size: 13px; color: rgba(255,255,255,0.55); margin-top: 7px; }

        /* ── CTA ── */
        .cta-section {
            background: var(--blue-25);
            border-top: 1px solid var(--blue-100);
            text-align: center; padding: 80px 60px;
        }
        .cta-section .section-title { max-width: 520px; margin: 0 auto 14px; }
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
            background: var(--white); border: 1px solid var(--gray-200);
            color: var(--gray-800); padding: 13px 28px; border-radius: 9px;
            font-size: 15px; font-weight: 500; text-decoration: none;
            transition: background 0.15s;
        }
        .btn-cta-outline:hover { background: var(--gray-100); }

        /* ── FOOTER ── */
        .footer { background: var(--blue-900); padding: 50px 60px 30px; }
        .footer-top {
            display: grid; grid-template-columns: 1.5fr 1fr 1fr;
            gap: 40px; padding-bottom: 40px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        .footer-brand-name { font-size: 18px; font-weight: 700; color: var(--white); margin-bottom: 10px; }
        .footer-brand-desc { font-size: 13px; color: rgba(255,255,255,0.45); line-height: 1.7; max-width: 260px; }
        .footer-col-title {
            font-size: 12px; font-weight: 600; color: rgba(255,255,255,0.4);
            text-transform: uppercase; letter-spacing: 0.08em; margin-bottom: 16px;
        }
        .footer-col a {
            display: block; font-size: 14px; color: rgba(255,255,255,0.6);
            text-decoration: none; margin-bottom: 10px; transition: color 0.15s;
        }
        .footer-col a:hover { color: var(--white); }
        .footer-bottom {
            padding-top: 24px; display: flex; align-items: center; justify-content: space-between;
            font-size: 13px; color: rgba(255,255,255,0.35);
        }
    </style>
</head>
<body>

    <!-- ══════════ NAVBAR ══════════ -->
    <nav class="navbar">
        <a href="<%= request.getContextPath() %>/app/home/" class="nav-brand">
            <div class="nav-brand-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
            </div>
            <span class="nav-brand-name">MentorKE</span>
        </a>
        <div class="nav-links">
            <a href="<%= request.getContextPath() %>/app/home/">Home</a>
            <a href="/MentorKE/app/about/" class="active">About</a>
            <a href="<%= request.getContextPath() %>/app/login/" class="btn-outline">Login</a>
            <a href="<%= request.getContextPath() %>/app/register/" class="btn-primary">Get started</a>
        </div>
    </nav>

    <!-- ══════════ HERO ══════════ -->
    <section class="hero">
        <div class="hero-tag">
            <span class="hero-tag-dot"></span>
            Our story
        </div>
        <h1>Built for Kenya's <span>next generation</span> of leaders</h1>
        <p>MentorKE was created to break down barriers to mentorship and give every young Kenyan access to the guidance they deserve.</p>
    </section>

    <!-- ══════════ OUR STORY ══════════ -->
    <section class="story-section">
        <div class="inner">
            <span class="section-label">Who we are</span>
            <div class="story-grid">
                <div class="story-text">
                    <h2 class="section-title">Mentorship shouldn't be a privilege</h2>
                    <p>MentorKE was founded on a simple observation: access to quality mentorship in Kenya is deeply unequal. Those with the right networks thrive, while talented individuals without connections struggle to find their footing.</p>
                    <p>We built MentorKE to change that — a structured, technology-driven platform that connects ambitious Kenyans with experienced professionals who are genuinely invested in their growth.</p>
                    <p>From Nairobi to Eldoret, from tech to healthcare, our platform makes it possible for any young person to find a mentor in their field and start building the future they envision.</p>
                </div>
                <div class="story-visual">
                    <div class="story-tile highlight">
                        <div class="story-tile-num">500+</div>
                        <div class="story-tile-label">Active mentors</div>
                    </div>
                    <div class="story-tile">
                        <div class="story-tile-num">2K+</div>
                        <div class="story-tile-label">Mentees enrolled</div>
                    </div>
                    <div class="story-tile">
                        <div class="story-tile-num">47</div>
                        <div class="story-tile-label">Counties reached</div>
                    </div>
                    <div class="story-tile highlight">
                        <div class="story-tile-num">95%</div>
                        <div class="story-tile-label">Satisfaction rate</div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- ══════════ MISSION & VALUES ══════════ -->
    <section class="values-section">
        <div class="inner">
            <span class="section-label">Mission & values</span>
            <h2 class="section-title">What drives everything we do</h2>
            <p class="section-sub">Our platform is built around a set of core principles that shape every feature, every match, and every interaction.</p>

            <div class="values-grid">
                <div class="value-card">
                    <div class="value-icon vi-blue">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#1565c0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                    </div>
                    <h3>Our mission</h3>
                    <p>To make mentorship accessible, structured, and impactful for every young Kenyan — regardless of background, location, or network.</p>
                </div>
                <div class="value-card">
                    <div class="value-icon vi-teal">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#0f6e56" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                    </div>
                    <h3>Inclusivity</h3>
                    <p>We believe great mentorship should be available to everyone. Our platform is designed to reach mentees across all 47 counties of Kenya.</p>
                </div>
                <div class="value-card">
                    <div class="value-icon vi-green">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#15803d" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
                    </div>
                    <h3>Growth-focused</h3>
                    <p>Every feature we build — goal tracking, session management, progress dashboards — is designed to drive measurable growth for mentees.</p>
                </div>
                <div class="value-card">
                    <div class="value-icon vi-amber">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#b45309" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
                    </div>
                    <h3>Trust & safety</h3>
                    <p>All mentors are reviewed and verified before joining our platform. We take the safety of our community seriously at every step.</p>
                </div>
                <div class="value-card">
                    <div class="value-icon vi-purple">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#7c3aed" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                    </div>
                    <h3>Structure</h3>
                    <p>Unlike informal mentorship, MentorKE provides the tools to set goals, track sessions, and measure progress in a structured, accountable way.</p>
                </div>
                <div class="value-card">
                    <div class="value-icon vi-red">
                        <svg viewBox="0 0 24 24" fill="none" stroke="#b91c1c" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
                    </div>
                    <h3>Community</h3>
                    <p>MentorKE is more than a platform — it's a growing community of professionals and learners invested in lifting each other up.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- ══════════ TEAM ══════════ -->
    <section class="team-section">
        <div class="inner">
            <span class="section-label">Our team</span>
            <h2 class="section-title">The people behind MentorKE</h2>
            <p class="section-sub">A passionate team of Kenyans committed to democratising access to mentorship through technology.</p>

            <div class="team-grid">
                <div class="team-card">
                    <div class="team-avatar">SO</div>
                    <div class="team-name">Sharon Opudo</div>
                    <div class="team-role">Founder & CEO</div>
                    <div class="team-bio">Former software engineer turned social entrepreneur.Sharon built MentorKE after struggling to find mentors in her early career.</div>
                </div>
                <div class="team-card">
                    <div class="team-avatar">DK</div>
                    <div class="team-name">David Kipchoge</div>
                    <div class="team-role">Head of Technology</div>
                    <div class="team-bio">Full-stack developer with 10+ years building platforms in East Africa. David leads all engineering and infrastructure at MentorKE.</div>
                </div>
                <div class="team-card">
                    <div class="team-avatar">GO</div>
                    <div class="team-name">Grace Otieno</div>
                    <div class="team-role">Head of Partnerships</div>
                    <div class="team-bio">Grace brings corporate and NGO partnerships together to expand MentorKE's mentor network and funding opportunities.</div>
                </div>
            </div>
        </div>
    </section>

    <!-- ══════════ IMPACT ══════════ -->
    <section class="impact-section">
        <div class="inner">
            <span class="section-label">Our impact</span>
            <h2 class="section-title">Growing across Kenya</h2>
            <p class="section-sub">Since launching, MentorKE has been making a measurable difference in the lives of young Kenyans.</p>

            <div class="impact-grid">
                <div class="impact-card">
                    <div class="impact-num">500+</div>
                    <div class="impact-label">Verified mentors</div>
                </div>
                <div class="impact-card">
                    <div class="impact-num">2,000+</div>
                    <div class="impact-label">Mentees enrolled</div>
                </div>
                <div class="impact-card">
                    <div class="impact-num">47</div>
                    <div class="impact-label">Counties reached</div>
                </div>
                <div class="impact-card">
                    <div class="impact-num">95%</div>
                    <div class="impact-label">Mentee satisfaction</div>
                </div>
            </div>
        </div>
    </section>

    <!-- ══════════ CTA ══════════ -->
    <section class="cta-section">
        <div class="inner">
            <span class="section-label">Join us</span>
            <h2 class="section-title">Be part of Kenya's mentorship movement</h2>
            <p class="section-sub">Whether you're looking for guidance or ready to give back — MentorKE has a place for you.</p>
            <div class="cta-btns">
                <a href="<%= request.getContextPath() %>/app/register/" class="btn-cta-primary">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                    Create a free account
                </a>
                <a href="<%= request.getContextPath() %>/app/home/" class="btn-cta-outline">Back to home</a>
            </div>
        </div>
    </section>

    <!-- ══════════ FOOTER ══════════ -->
    <footer class="footer">
        <div class="inner">
            <div class="footer-top">
                <div>
                    <div class="footer-brand-name">MentorKE</div>
                    <p class="footer-brand-desc">Connecting mentors and mentees across Kenya for growth, guidance, and lasting opportunity.</p>
                </div>
                <div class="footer-col">
                    <div class="footer-col-title">Platform</div>
                    <a href="<%= request.getContextPath() %>/app/home/">Home</a>
                    <a href="/MentorKE/app/about/">About</a>
                    <a href="<%= request.getContextPath() %>/app/register/">Register</a>
                    <a href="<%= request.getContextPath() %>/app/login/">Login</a>
                </div>
                <div class="footer-col">
                    <div class="footer-col-title">Roles</div>
                    <a href="<%= request.getContextPath() %>/app/register/">Join as a mentee</a>
                    <a href="<%= request.getContextPath() %>/app/register/">Join as a mentor</a>
                    <a href="<%= request.getContextPath() %>/app/login/?role=admin">Admin login</a>
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